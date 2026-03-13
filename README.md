# Atlas Index

Atlas Index is an open-source, community-driven ranking system designed for the **MBAACC Community Edition**. Its goal is to provide a transparent and low-friction way for players to participate in ranked matches while preserving the community's existing matchmaking workflow.

Rather than replacing current tools or matchmaking practices, Atlas Index is designed to function as a passive layer on top of the existing community infrastructure built around CCCaster.

## Character-Based Ranking

Atlas Index uses a **per-character rating system** instead of a single global rating per player.

In *Melty Blood Actress Again Current Code*, characters are defined not only by their base identity but also by their **Moon style**. Each Moon and character combination is treated as a distinct character within the ranking system.

As a result, players can have multiple Elo ratings depending on the characters they play.

For example:

| Player | Character | Rating |
|------|------|------|
| PlayerA | F-Akiha | 1650 |
| PlayerA | C-Akiha | 1420 |

This approach allows the ranking system to better reflect player skill with specific characters rather than aggregating performance across different playstyles.

## How Ranked Mode Works

Ranked matches must be **explicitly acknowledged by both players**.

Before a match begins, the system verifies whether the opponent is also participating in ranked mode. This ensures that players know in advance whether the upcoming match will count toward ranking.

If both players are participating, the match is eligible to be recorded as a ranked match. If one player is not participating, the match proceeds normally but will not be registered in the ranking system.

## Atlas Observer

[Atlas Observer](https://github.com/kuronek0-da/atlas-observer) is a companion client responsible for monitoring game state and reporting match results.

It reads memory from MBAA and CCCaster in order to:

- Check whether the opponent is participating in ranked mode
- Validate that the match is a valid netplay game
- Automatically submit match results to the ranking server

This allows match reporting to happen automatically without requiring manual input from players.

## Discord Integration

Atlas Index integrates with Discord for player identity.

Player profiles are associated with their **Discord ID and username**, which serve as the primary identifier within the system.