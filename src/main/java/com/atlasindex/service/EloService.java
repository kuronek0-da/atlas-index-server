package com.atlasindex.service;

import org.springframework.stereotype.Service;

import com.atlasindex.model.enums.EloResult;

/**
 * Rules:
 *  - Ratings are per (player, character, moon) combo
 *  - Default starting ELO: 1000
 *  - Fixed K-factor: 32
 *  - Match format: Best of 3 (first to 2 rounds wins)
 *  - No character matchup adjustments
 */
@Service
public class EloService {
    public static final int STARTING_ELO = 1000;
    public static final int K_FACTOR = 32;
    public static final int ROUNDS_TO_WIN = 2;

    /**
     * Expected score for player A against player B.
     * Returns a value in (0, 1).
     */
    private double expectedScore(double ratingA, double ratingB) {
        return 1.0 / (1.0 + Math.pow(10.0, (ratingB - ratingA) / 400.0));
    }

    /**
     * Calculates new ELO for a player after a match
     * @param currentRating current ELO
     * @param opponentRating opponent's current ELO
     * @param result result from this player perspective
     * @return rounded updated ELO
     */
    private int calculateNewRating(int currentRating, int opponentRating, EloResult result) {
        double expected = expectedScore(currentRating, opponentRating);
        double actual = result.getScore();
        double newRating = currentRating + K_FACTOR * (actual - expected);
        return (int) Math.round(newRating);
    }

    /**
     * Calculates and returns new player ratings after a match
     * @param ratingA Player A current ELO
     * @param ratingB Player B current ELO
     * @param outcome Who won
     */
    public EloUpdate processMatch(int ratingA, int ratingB, MatchOutcome outcome) {
        int newRatingA = calculateNewRating(ratingA, ratingB, outcome.resultForA());
        int newRatingB = calculateNewRating(ratingB, ratingA, outcome.resultForB());
        return new EloUpdate(newRatingA, newRatingB);
    }
    
    /**
     * Result of a match from the player's perspective
     */
    public record MatchOutcome(int roundsWonA, int roundsWonB) {
        public MatchOutcome {
            if (roundsWonA < 0 || roundsWonB < 0) {
                throw new IllegalArgumentException("Rounds count cannot be negative");
            }

            boolean aWon = roundsWonA == ROUNDS_TO_WIN;
            boolean bWon = roundsWonB == ROUNDS_TO_WIN;
            if (!aWon && !bWon) {
                throw new IllegalArgumentException(
                    "Invalid FT3 result: (%d-%d). One player must have %d wins."
                        .formatted(roundsWonA, roundsWonB, ROUNDS_TO_WIN)
                );
            }
            if (aWon && bWon) {
                throw new IllegalArgumentException("Both players cannot win simultaneously.");
            }
        }

        public EloResult resultForA() {
            return roundsWonA == ROUNDS_TO_WIN ? EloResult.WIN : EloResult.LOSS;
        }

        public EloResult resultForB() {
            return roundsWonB == ROUNDS_TO_WIN ? EloResult.WIN : EloResult.LOSS;
        }
    }

    public record EloUpdate(int newRatingA, int newRatingB) {
        public int deltaA(int previousRatingA) {
            return newRatingA - previousRatingA;
        }

        public int deltaB(int previousRatingB) {
            return newRatingB - previousRatingB;
        }
    }

}