"""What the smoke test (smoke_test.py) does with this mod's features. Each mod fills this file in.

The smoke test calls these in order:
  configure(folder)                 before the modded server starts: write config files into folder/config
  with_mod(server, check)           the modded server runs, without players: leave in the world what the mod saves
  with_client(server, name, check)  a vanilla client is online as `name` (an operator): what players without the mod
                                    see and do
  on_vanilla(server, check)         the same world on the vanilla server, the mod removed: what must have survived,
                                    and what must be plain vanilla again
`server.command(line)` types a console command; `server.run(line, expect, what)` types one and checks that an output
line matches the regex `expect`. `check(ok, what)` records a check.

expected_problems and expected_vanilla_warnings filter out log lines this scenario causes on purpose. TEST_MODS lists
other mods (Modrinth project ids) the modded servers run with, for testing only.

The example below stands for the rule every mod follows (../CLAUDE.md, "The world stays vanilla-valid"): the mod's own
data rides along in `custom_data`, which vanilla keeps but ignores.
"""

from pathlib import Path

# Mods the servers run with besides ours (from Modrinth, newest for the version and loader, test only): Lithium, which
# many servers run, changes how block entities tick and hoppers move items.
TEST_MODS = ['lithium']

# A spot in the flat world above the grass (y = -61), kept loaded with /forceload.
CHEST = '0 -60 0'


def configure(folder: Path) -> None:
	pass


def with_mod(server, check) -> None:
	server.run('forceload add 0 0', r'[Mm]arked chunk|already marked', 'the test chunk stays loaded')
	server.run(f'setblock {CHEST} minecraft:chest', r'Changed the block', 'a chest is placed')
	server.run(f'item replace block {CHEST} container.0 with minecraft:name_tag[minecraft:custom_data={{nr137_lib:{{test:1b}}}}]',
		r'Replaced \d+ slot', 'an item with the mod\'s custom_data goes into the chest')


def with_client(server, name: str, check) -> None:
	server.run(f'give {name} minecraft:name_tag[minecraft:custom_data={{nr137_lib:{{test:1b}}}}]',
		rf'Gave 1 .* to {name}', 'the vanilla client gets an item with the mod\'s custom_data')


def on_vanilla(server, check) -> None:
	server.run(f'data get block {CHEST} Items[0]', r'nr137_lib', 'vanilla keeps the mod\'s custom_data on the item')


def expected_problems(lines: list[str]) -> list[str]:
	return lines


def expected_vanilla_warnings(lines: list[str]) -> list[str]:
	return lines
