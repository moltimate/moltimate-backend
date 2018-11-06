package org.moltimate.moltimatebackend.constant;

import lombok.Getter;

@Getter
public enum AminoAcidType {
    ALA("A", "Alanine"),
    ARG("B", "Arginine"),
    ASN("C", "Asparagine"),
    ASP("D", "Aspartic acid"),
    CYS("E", "Cysteine"),
    GLN("F", "Glutamine"),
    GLU("G", "Glutamic acid"),
    GLY("H", "Glycine"),
    HIS("I", "Histidine"),
    ILE("J", "Isoleucine"),
    LEU("K", "Leucine"),
    LYS("L", "Lysine"),
    MET("M", "Methionine"),
    PHE("N", "Phenylalanine"),
    PRO("O", "Proline"),
    SER("P", "Serine"),
    THR("Q", "Threonine"),
    TRP("R", "Tryptophan"),
    TYR("S", "Tyrosine"),
    VAL("T", "Valine");

    private String levenshteinValue;
    private String fullName;
    private String codeName;

    AminoAcidType(String levenshteinValue, String fullName) {
        this.levenshteinValue = levenshteinValue;
        this.fullName = fullName;
        this.codeName = name();
    }

    public static AminoAcidType fromCodeName(String codeName) {
        return AminoAcidType.valueOf(codeName.toUpperCase());
    }
}
