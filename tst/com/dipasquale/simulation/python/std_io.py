from __future__ import annotations

import enum
import io
import os
import re
import sys
from abc import ABC, abstractmethod
from typing import Iterable


class StatusType(enum.Enum):
    STAND_BY = 0
    HANDLE_REQUEST = 1
    TERMINATE = 2
    ECHO = 3


class Request:
    def __init__(self: Request, method: str, resource: str, headers: dict[str, str], body: str) -> None:
        self.method: str = method
        self.resource: str = resource
        self.headers: dict[str, str] = headers
        self.body: str = body


class Response:
    def __init__(self: Response) -> None:
        self.status_code: int = 200
        self.headers: dict[str, str] = {}
        self.body: io.StringIO = io.StringIO()


class RequestBuilder:
    _MAPPING_PATTERN: re.Pattern = re.compile(r"^(GET|POST)\s+([/\w]+)$")
    _HEADERS_PATTERN: re.Pattern = re.compile(r"^([^:\s]+)\s*:\s*([^\r\n]*)$")
    END_MESSAGE_TOKEN: str = "END-MSG"
    TERMINATE_TOKEN: str = "TERMINATE"
    ECHO_TOKEN: str = "ECHO"

    def __init__(self: RequestBuilder) -> None:
        self._status_type: StatusType = StatusType.STAND_BY
        self._request: Request = None
        self._line_buffer: list[str] = []

    @staticmethod
    def _build_request(line_buffer: list[str]) -> Request:
        method: str = None
        resource: str = None
        headers: dict[str, str] = {}
        body_length: int = 0
        body: io.StringIO = io.StringIO()
        is_building_header: bool = True

        if len(line_buffer) > 0:
            mapping_match: re.Match = RequestBuilder._MAPPING_PATTERN.search(line_buffer[0])

            if not mapping_match:
                raise ValueError(f"unable to parse mapping: {line_buffer[0]}")

            method = mapping_match.group(1).upper()
            resource = mapping_match.group(2)

            for line in line_buffer[1:]:
                if is_building_header:
                    if line == "=" and method == "GET":
                        raise ValueError("body not allowed for GET methods")

                    if line != '=':
                        header_match: re.Match = RequestBuilder._HEADERS_PATTERN.search(line)

                        if not header_match:
                            raise ValueError(f"unable to parse header: {line}")

                        headers[header_match.group(1).lower()] = header_match.group(2)
                    else:
                        is_building_header = False
                else:
                    if body_length > 0:
                        body.write(os.linesep)

                    body.write(line)
                    body_length += len(line)

        return Request(method, resource, headers, body.getvalue())

    def append_line(self: RequestBuilder, line: str) -> StatusType:
        if line == RequestBuilder.END_MESSAGE_TOKEN:
            self._status_type = StatusType.HANDLE_REQUEST
            self._request = RequestBuilder._build_request(self._line_buffer)
            self._line_buffer = []
        elif line == RequestBuilder.TERMINATE_TOKEN:
            self._status_type = StatusType.TERMINATE
            self._request = None
            self._line_buffer = []
        elif line == RequestBuilder.ECHO_TOKEN:
            self._status_type = StatusType.ECHO
            self._request = Request(None, None, None, os.linesep.join(self._line_buffer))
            self._line_buffer = []
        elif line:
            self._line_buffer.append(line)

        return self._status_type

    def remove_built_request(self: RequestBuilder) -> Request:
        request: Request = self._request

        self._status_type = StatusType.STAND_BY
        self._request = None

        return request


class RequestHandler(ABC):
    @abstractmethod
    def handle(self: RequestHandler, request: Request, response: Response) -> bool:
        pass


class Controller(ABC):
    @abstractmethod
    def enact(self: Controller, request: Request, response: Response) -> None:
        pass


class RoutingRequestHandler(RequestHandler):
    def __init__(self: RoutingRequestHandler) -> None:
        super().__init__()
        self._controllers: dict[str, Controller] = {}

    def add_controller(self: RoutingRequestHandler, method: str, resource: str, controller: Controller) -> None:
        controller_id: str = f"{method} {resource}"

        self._controllers[controller_id] = controller

    def handle(self: RoutingRequestHandler, request: Request, response: Response) -> bool:
        controller_id: str = f"{request.method} {request.resource}"
        controller: Controller = self._controllers[controller_id]

        if not controller:
            return False

        controller.enact(request, response)

        return True


class ServerStdIO:
    def __init__(self: ServerStdIO) -> None:
        self._request_builder: RequestBuilder = RequestBuilder()
        self._request_handlers: list[RequestHandler] = []
        self._input_log: io.TextIOWrapper = None
        self._output_log: io.TextIOWrapper = None

    def add_input_log_file(self: ServerStdIO, file_name: str) -> None:
        self._input_log = open(file_name, "a")

    def add_output_log_file(self: ServerStdIO, file_name: str) -> None:
        self._output_log = open(file_name, "a")

    def add_request_handler(self: ServerStdIO, request_handler: RequestHandler) -> None:
        self._request_handlers.append(request_handler)

    @staticmethod
    def _read_line_from_input() -> Iterable[str]:
        return sys.stdin

    def _write_to_input_log_if_available(self: ServerStdIO, line: str) -> None:
        if self._input_log:
            self._input_log.write(f"*{line}*\n")

    def _write_to_output(self: ServerStdIO, line: str) -> None:
        sys.stdout.write(f"{line}\n")

        if self._output_log:
            self._output_log.write(f"*{line}*\n")

        sys.stdout.flush()

    def _listen_internal(self: ServerStdIO) -> None:
        for line in ServerStdIO._read_line_from_input():
            fixed_line: str = line.rstrip()
            self._write_to_input_log_if_available(fixed_line)

            status_type: StatusType = self._request_builder.append_line(fixed_line)

            if status_type == StatusType.HANDLE_REQUEST:
                request: Request = self._request_builder.remove_built_request()
                response: Response = Response()
                handled: bool = False
                unhandled_exception: Exception = None

                for request_handler in self._request_handlers:
                    try:
                        handled = request_handler.handle(request, response)
                    except Exception as e:
                        unhandled_exception = e

                    if handled or unhandled_exception:
                        break

                if handled:
                    self._write_to_output(str(response.status_code))

                    for key, value in response.headers.items():
                        self._write_to_output(f"{key}: {value}")

                    self._write_to_output("=")
                    self._write_to_output(response.body.getvalue())
                elif unhandled_exception:
                    self._write_to_output("500")
                    self._write_to_output("content-type: text")
                    self._write_to_output("=")
                    self._write_to_output(str(unhandled_exception))
                else:
                    self._write_to_output("404")

                self._write_to_output(RequestBuilder.END_MESSAGE_TOKEN)

            if status_type == StatusType.TERMINATE:
                return

            if status_type == StatusType.ECHO:
                request: Request = self._request_builder.remove_built_request()
                self._write_to_output("200")
                self._write_to_output(request.body)
                self._write_to_output(RequestBuilder.END_MESSAGE_TOKEN)

    def listen(self: ServerStdIO) -> None:
        try:
            self._listen_internal()
        finally:
            if self._input_log:
                self._input_log.close()

            if self._output_log:
                self._output_log.close()


class MockServerStdIO:
    def __init__(self: MockServerStdIO) -> None:
        self._stdin: list[str] = []

    def add_input_line(self: MockServerStdIO, text: str) -> None:
        self._stdin.append(text)

    def add_end_of_message(self: MockServerStdIO) -> None:
        self._stdin.append(RequestBuilder.END_MESSAGE_TOKEN)

    def commit(self: MockServerStdIO) -> None:
        sys.stdin = self._stdin
