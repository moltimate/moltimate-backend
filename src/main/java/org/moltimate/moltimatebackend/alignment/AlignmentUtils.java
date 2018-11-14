package org.moltimate.moltimatebackend.alignment;

import org.biojava.nbio.structure.Group;
import org.moltimate.moltimatebackend.constant.AminoAcidType;
import org.moltimate.moltimatebackend.model.Residue;

import java.util.Arrays;
import java.util.List;

public class AlignmentUtils {

    public static String groupListToResString(List<Group> residues) {
        StringBuilder stringBuilder = new StringBuilder();
        residues.forEach(residue -> stringBuilder.append(AminoAcidType.fromCodeName(residue.getChemComp()
                                                                                           .getThree_letter_code())
                                                                      .getLevenshteinValue()));
        return stringBuilder.toString();
    }

    public static String residueListToResString(List<Residue> resList) {
        StringBuilder stringBuilder = new StringBuilder();
        resList.forEach(residue -> stringBuilder.append(AminoAcidType.fromCodeName(residue.getResidueName())
                                                                     .getLevenshteinValue()));
        return stringBuilder.toString();
    }

    public static int levensteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                     .min()
                     .orElse(Integer.MAX_VALUE);
    }
}
