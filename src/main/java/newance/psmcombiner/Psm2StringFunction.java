/*
Copyright (C) SIB - Swiss Institute of Bioinformatics, Lausanne, Switzerland
Copyright (C) LICR - Ludwig Institute of Cancer Research, Lausanne, Switzerland
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
*/

package newance.psmcombiner;

import newance.mzjava.mol.Peptide;
import newance.mzjava.mol.modification.ModAttachment;
import newance.mzjava.mol.modification.Modification;
import newance.psmconverter.PeptideSpectrumMatch;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Markus Müller
 */

public class Psm2StringFunction implements BiFunction<String, List<PeptideSpectrumMatch>, String> {

    public enum TabStringMode {COMBINED, COMET, MAXQUANT};

    protected final TabStringMode tabStringMode;
    protected final GroupedFDRCalculator groupedFDRCalculator;


    public Psm2StringFunction(TabStringMode tabStringMode, GroupedFDRCalculator groupedFDRCalculator) {
        this.tabStringMode = tabStringMode;
        this.groupedFDRCalculator = groupedFDRCalculator;
    }

    public Psm2StringFunction(TabStringMode tabStringMode) {
        this.tabStringMode = tabStringMode;
        this.groupedFDRCalculator = null;
    }

    @Override
    public String apply(String specID, List<PeptideSpectrumMatch> peptideSpectrumMatchData) {

        String txt = "";
        for (PeptideSpectrumMatch psm : peptideSpectrumMatchData) {
            txt += getTabString(specID, psm);
        }

        return txt;
    }


    public String getHeader() {

        if (tabStringMode == TabStringMode.COMET) {
            if (groupedFDRCalculator != null)
                return "Spectrum\tScanNr\tCharge\tRT\tNeutralMass\tPeptide\tSequence\tPeptideMass\tModifName\tModifPosition\tModifMass\tModifAA\tProteins\t" +
                        "IsVariant\tVariantPosition\tWTAA\tIsDecoy\tRank\tXCorr\tDeltaCn\tSpScore\tExpect\tmassdiff\ttot_num_ions\tnum_matched_ions\tlFDR";
            else
                return "Spectrum\tScanNr\tCharge\tRT\tNeutralMass\tPeptide\tSequence\tPeptideMass\tModifName\tModifPosition\tModifMass\tModifAA\tProteins\t" +
                        "IsVariant\tVariantPosition\tWTAA\tIsDecoy\tRank\tXCorr\tDeltaCn\tSpScore\tExpect\tmassdiff\ttot_num_ions\tnum_matched_ions";
        }
        else if (tabStringMode == TabStringMode.MAXQUANT)
            return "Spectrum\tScanNr\tCharge\tRT\tNeutralMass\tPeptide\tSequence\tPeptideMass\tModifName\tModifPosition\tModifMass\tModifAA\tProteins\t" +
                    "IsVariant\tIsDecoy\tRank\tMass.Error[ppm]\tScore\tDelta.score\tLocalization.prob";
        else {
            if (groupedFDRCalculator != null)
                return "Spectrum\tScanNr\tCharge\tRT\tNeutralMass\tPeptide\tSequence\tPeptideMass\tModifName\tModifPosition\tModifMass\tModifAA\tProteins\t" +
                        "IsVariant\tVariantPosition\tWTAA\tIsDecoy\tComet.Rank\tComet.XCorr\tComet.DeltaCn\tComet.SpScore\tComet.Expect\tComet.massdiff\tComet.tot_num_ions\t" +
                        "Comet.num_matched_ions\tComet.lFDR\tMaxQuant.Mass.Error[ppm]\tMaxQuant.Score\tMaxQuant.Delta.score\tMaxQuant.Localization.prob";
            else
                return "Spectrum\tScanNr\tCharge\tRT\tNeutralMass\tPeptide\tSequence\tPeptideMass\tModifName\tModifPosition\tModifMass\tModifAA\tProteins\t" +
                        "IsVariant\tVariantPosition\tWTAA\tIsDecoy\tComet.Rank\tComet.XCorr\tComet.DeltaCn\tComet.SpScore\tComet.Expect\tComet.massdiff\tComet.tot_num_ions\t" +
                        "Comet.num_matched_ions\tMaxQuant.Mass.Error[ppm]\tMaxQuant.Score\tMaxQuant.Delta.score\tMaxQuant.Localization.prob";
        }
    }

    public String getTabString(String specID, PeptideSpectrumMatch psm) {

        String rt = String.format("%.5f",psm.getRetentionTime());
        String mass = String.format("%.5f",psm.getNeutralPrecMass());

        if (tabStringMode == TabStringMode.COMET)
            return specID+"\t"+psm.getScanNr()+"\t"+psm.getCharge()+"\t"+rt+"\t"+mass+"\t"+
                    getCometString(psm);
        if (tabStringMode == TabStringMode.MAXQUANT)
            return specID+"\t"+psm.getScanNr()+"\t"+psm.getCharge()+"\t"+rt+"\t"+mass+"\t"+
                    getMaxQuantString(psm);
        else
            return specID+"\t"+psm.getScanNr()+"\t"+psm.getCharge()+"\t"+rt+"\t"+mass+"\t"+
                    getCometCombString(psm)+"\t"+getMaxQuantCombString(psm);

    }

    protected String getCometCombString(PeptideSpectrumMatch psm) {

        String protACs = psm.getProteinAcc().toString();
        int rank = (int) psm.getScore("rank");
        String lfdrStr = (groupedFDRCalculator==null)?"":String.format("\t%.5f",groupedFDRCalculator.getLocalFDR(psm));
        String expectStr = String.format("%.5f",psm.getScore("expect"));
        Peptide peptide = psm.getPeptide();
        String pepMass = String.format("%.5f",peptide.getMolecularMass());
        String modifString = getModifString(peptide);
        String variantString = getVariantString(psm);

        return  psm.getPeptide().toString()+"\t"+psm.getPeptide().toSymbolString()+"\t"+pepMass+"\t"+modifString+"\t"+protACs+"\t"+
                variantString+"\t"+ psm.isDecoy()+"\t"+rank+"\t"+ psm.getScore("xcorr")+"\t"+psm.getScore("deltacn")+"\t"+
                psm.getScore("spscore")+"\t"+expectStr+"\t+"+ psm.getScore("mass_diff")+"\t"+(int)psm.getScore("tot_num_ions")+"\t"+
                (int)psm.getScore("matched_num_ions") + lfdrStr; // no tab before lfdrStr
    }

    protected String getMaxQuantCombString(PeptideSpectrumMatch psm) {

        return psm.getScore("Mass Error [ppm]")+"\t"+psm.getScore("Score")+"\t"+psm.getScore("Delta score")+"\t"+
                psm.getScore("Localization prob");
    }

    protected String getCometString(PeptideSpectrumMatch psm) {

        String protACs = psm.getProteinAcc().toString();
        int rank = (int) psm.getScore("rank");
        String lfdrStr = (groupedFDRCalculator==null)?"":String.format("\t%.5f",groupedFDRCalculator.getLocalFDR(psm));
        String expectStr = String.format("%.5f",psm.getScore("expect"));
        Peptide peptide = psm.getPeptide();
        String pepMass = String.format("%.5f",peptide.getMolecularMass());
        String modifString = getModifString(peptide);
        String variantString = getVariantString(psm);

        return  psm.getPeptide().toString()+"\t"+psm.getPeptide().toSymbolString()+"\t"+pepMass+"\t"+modifString+"\t"+protACs+"\t"+variantString+"\t"+psm.isDecoy()+"\t"+
                rank+"\t"+psm.getScore("xcorr")+"\t"+psm.getScore("deltacn")+"\t"+psm.getScore("spscore")+"\t"+expectStr+"\t+" +
                psm.getScore("mass_diff")+"\t"+(int)psm.getScore("tot_num_ions")+"\t"+(int)psm.getScore("matched_num_ions")+lfdrStr; // no tab before lfdrStr
    }

    protected String getMaxQuantString(PeptideSpectrumMatch psm) {

        String protACs = psm.getProteinAcc().toString();
        boolean isVariant = (protACs.contains("variant__"));
        int rank = (int) psm.getScore("rank");
        Peptide peptide = psm.getPeptide();
        String pepMass = String.format("%.5f",peptide.getMolecularMass());
        String modifString = getModifString(peptide);

        return  psm.getPeptide().toString()+"\t"+psm.getPeptide().toSymbolString()+"\t"+pepMass+"\t"+modifString+"\t"+protACs+"\t"+isVariant+"\t"+psm.isDecoy()+"\t"+
                rank+"\t"+psm.getScore("Mass Error [ppm]")+"\t"+psm.getScore("Score")+"\t"+psm.getScore("Delta score")+"\t"+
                psm.getScore("Localization prob");
    }

    private String getModifString(Peptide peptide)  {

        if (!peptide.hasModifications()) return "NA\tNA\tNA\tNA";

        String modifNames = "";
        String modifPos = "";
        String modifMass = "";
        String modifAA = "";

        if (peptide.hasModificationAt(ModAttachment.N_TERM)) {
            for (Modification modif : peptide.getModifications(ModAttachment.nTermSet)) {
                modifNames = (modifNames.isEmpty())?modif.getLabel():","+modif.getLabel();
                modifPos = (modifPos.isEmpty())?"0":",0";
                String massStr = String.format("%.5f",modif.getMolecularMass());
                modifMass = (modifMass.isEmpty())?massStr:","+massStr;
                modifAA = (modifAA.isEmpty())?"NT":",NT";
            }
        }

        if (peptide.hasModificationAt(ModAttachment.SIDE_CHAIN)) {
            for (int i : peptide.getModificationIndexes(ModAttachment.sideChainSet)) {

                for (Modification modif : peptide.getModificationsAt(i, ModAttachment.sideChainSet)) {
                    modifNames = (modifNames.isEmpty())?modif.getLabel():","+modif.getLabel();
                    String posStr = String.format("%d",i+1);
                    modifPos = (modifPos.isEmpty())?posStr:","+posStr;
                    String massStr = String.format("%.5f",modif.getMolecularMass());
                    modifMass = (modifMass.isEmpty())?massStr:","+massStr;
                    modifAA = (modifAA.isEmpty())?peptide.getSymbol(i).getSymbol():","+peptide.getSymbol(i).getSymbol();
                }
            }
        }

        if (peptide.hasModificationAt(ModAttachment.C_TERM)) {
            for (Modification modif : peptide.getModifications(ModAttachment.cTermSet)) {
                modifNames = (modifNames.isEmpty())?modif.getLabel():","+modif.getLabel();
                String posStr = String.format("%d",peptide.size()+1);
                modifPos = (modifPos.isEmpty())?posStr:","+posStr;
                String massStr = String.format("%.5f",modif.getMolecularMass());
                modifMass = (modifMass.isEmpty())?massStr:","+massStr;
                modifAA = (modifAA.isEmpty())?"CT":",CT";
            }
        }

        return modifNames+"\t"+modifPos+"\t"+modifMass+"\t"+modifAA;
    }

    private String getVariantString(PeptideSpectrumMatch psm) {
        if (!psm.isVariant()) return "false\tNA\tNA";

        String variantStr = "true";

        String posStr = "";
        for (Integer pos : psm.getVariantPositions()) {
            posStr += (posStr.isEmpty())?(pos+1):","+(pos+1);
        }

        String wtaaStr = "";
        for (Character aa : psm.getVariantWTAAs()) {
            wtaaStr += (wtaaStr.isEmpty())?aa:","+aa;
        }

        return variantStr+"\t"+posStr+"\t"+wtaaStr;
    }

}
