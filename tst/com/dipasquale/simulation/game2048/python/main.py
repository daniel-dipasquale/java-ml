from __future__ import annotations
import sys

sys.path.insert(1, "../../python")

import std_io
import re
import time
import selenium
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys

class ValuedTile:
    def __init__(self: ValuedTile, value: int, column: int, row: int) -> None:
        self.value: int = value
        self.column: int = column
        self.row: int = row

    def __hash__(self: ValuedTile):
        return hash(self.value) ^ hash(self.column) ^ hash(self.row)

    def __eq__(self: ValuedTile, other: object):
        return isinstance(other, ValuedTile) and self.value == other.value and self.column == other.column and self.row == other.row

    def __str__(self: ValuedTile) -> str:
        return f"[{self.value},{self.column},{self.row}]"


class Game2048:
    _SCORE_PATTERN: re.Pattern = re.compile(f"^(\d+)(?:[^\d]+\d+)?")
    _VALUED_TILE_PATTERN: re.Pattern = re.compile(r"^tile\s+tile-(\d+)\s+tile-position-(\d+)-(\d+)")

    def __init__(self: Game2048, driver: selenium.webdriver.chrome.webdriver.WebDriver) -> None:
        self._driver: selenium.webdriver.chrome.webdriver.WebDriver = driver

    def load(self: Game2048) -> None:
        self._driver.get("https://play2048.co/")

    def restart(self: Game2048) -> None:
        restart_button_elements: list[selenium.webdriver.remote.webelement.WebElement] = self._driver.find_elements(By.CSS_SELECTOR, "a.restart-button")

        restart_button_elements[0].click()

    def extract_score(self: Game2048) -> int:
        score_container_element: selenium.webdriver.remote.webelement.WebElement = self._driver.find_element(By.CSS_SELECTOR, ".score-container")
        score_text: str = score_container_element.text
        score_match: re.Match = Game2048._SCORE_PATTERN.search(score_text)

        if not score_match:
            raise ValueError(f"unable to extract the score {score_text}")

        return int(score_match.group(1))

    @staticmethod
    def _create_valued_tile(valued_tile_element: selenium.webdriver.remote.webelement.WebElement) -> ValuedTile:
        valued_tile_css_class: str = valued_tile_element.get_attribute("class")
        valued_tile_match: re.Match = Game2048._VALUED_TILE_PATTERN.search(valued_tile_css_class)

        if not valued_tile_match:
            raise ValueError(f"class {valued_tile_css_class} could not be parsed")

        value: int = int(valued_tile_match.group(1))
        column: int = int(valued_tile_match.group(2))
        row: int = int(valued_tile_match.group(3))

        return ValuedTile(value, column, row)

    def _extract_valued_tiles(self: Game2048, css_selector: str) -> list[ValuedTile]:
        valued_tiles_elements: list[selenium.webdriver.remote.webelement.WebElement] = self._driver.find_elements(By.CSS_SELECTOR, css_selector)

        return [Game2048._create_valued_tile(valued_tile_element) for valued_tile_element in valued_tiles_elements]

    def extract_new_valued_tiles(self: Game2048) -> list[ValuedTile]:
        return self._extract_valued_tiles(".tile-container .tile-new")

    def extract_all_valued_tiles(self: Game2048) -> list[ValuedTile]:
        return self._extract_valued_tiles(".tile-container .tile")

    def move_left(self: Game2048) -> None:
        selenium.webdriver.ActionChains(self._driver).send_keys(Keys.ARROW_LEFT).perform()

    def move_up(self: Game2048) -> None:
        selenium.webdriver.ActionChains(self._driver).send_keys(Keys.ARROW_UP).perform()

    def move_right(self: Game2048) -> None:
        selenium.webdriver.ActionChains(self._driver).send_keys(Keys.ARROW_RIGHT).perform()

    def move_down(self: Game2048) -> None:
        selenium.webdriver.ActionChains(self._driver).send_keys(Keys.ARROW_DOWN).perform()


class BeginningGameController(std_io.Controller):
    def __init__(self: BeginningGameController, game: Game2048) -> None:
        super().__init__()
        self._game: Game2048 = game

    def enact(self: BeginningGameController, request: std_io.Request, response: std_io.Response) -> None:
        if game.extract_score() > 0 or len(game.extract_all_valued_tiles()) != 2:
            ready_to_respond: bool = False

            game.restart()

            while not ready_to_respond:
                time.sleep(0.1)

                ready_to_respond = game.extract_score() == 0 and len(game.extract_all_valued_tiles()) == 2

        response.headers["content-type"] = "text"

        for valued_tile in game.extract_new_valued_tiles():
            response.body.write(str(valued_tile))


class MovementGameController(std_io.Controller):
    @staticmethod
    def _create_movement_methods() -> dict[str, function]:
        return {
            "LEFT": getattr(Game2048, "move_left"),
            "UP": getattr(Game2048, "move_up"),
            "RIGHT": getattr(Game2048, "move_right"),
            "DOWN": getattr(Game2048, "move_down")
        }

    def __init__(self: MovementGameController, game: Game2048) -> None:
        super().__init__()
        self._game: Game2048 = game
        self._movement_methods: dict[str, function] = MovementGameController._create_movement_methods()

    def enact(self: MovementGameController, request: std_io.Request, response: std_io.Response) -> None:
        initial_score: int = game.extract_score()
        initial_valued_tiles: set[ValuedTile] = set(game.extract_all_valued_tiles())
        new_valued_tiles: list[ValuedTile] = None
        ready_to_respond: bool = False

        self._movement_methods[request.body](game)

        while not ready_to_respond:
            time.sleep(0.1)

            new_valued_tiles: list[ValuedTile] = game.extract_new_valued_tiles()
            ready_to_respond = (initial_score < game.extract_score() or initial_valued_tiles != set(game.extract_all_valued_tiles())) and len(new_valued_tiles) == 1

        response.headers["content-type"] = "text"

        for valued_tile in new_valued_tiles:
            response.body.write(str(valued_tile))


service: Service = Service(ChromeDriverManager().install())
options = Options()

options.add_argument('--log-level=3')
options.add_argument('--no-sandbox')
options.add_argument('--disable-dev-shm-usage')

driver: selenium.webdriver.chrome.webdriver.WebDriver = selenium.webdriver.Chrome(service=service, options=options)
game: Game2048 = Game2048(driver)
server: std_io.ServerStdIO = std_io.ServerStdIO()
routing_request_handler: std_io.RoutingRequestHandler = std_io.RoutingRequestHandler()

routing_request_handler.add_controller("POST", "/beginning", BeginningGameController(game))
routing_request_handler.add_controller("POST", "/movement", MovementGameController(game))
server.add_request_handler(routing_request_handler)
game.load()

try:
    server.listen()
except Exception as e:
    file = open("error.log.txt", "a")
    file.write(str(e))
    file.close()
finally:
    driver.close()
