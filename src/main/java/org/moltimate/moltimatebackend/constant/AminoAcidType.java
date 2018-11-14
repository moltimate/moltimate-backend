package org.moltimate.moltimatebackend.constant;

import lombok.Getter;

/**
 * This defines every amino acid, their unique character representation, and their fully-qualified name.
 */
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

    private String charMapping;
    private String fullName;

    AminoAcidType(String charMapping, String fullName) {
        this.charMapping = charMapping;
        this.fullName = fullName;
    }

    public static AminoAcidType fromCodeName(String codeName) {
        return AminoAcidType.valueOf(codeName.toUpperCase());
    }

    public static String getCharMapping(String codeName) {
        return fromCodeName(codeName).getCharMapping();
    }

    public static String getFullName(String codeName) {
        return fromCodeName(codeName).getFullName();
    }
}
