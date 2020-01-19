/**
 * Copyright (C) 2019, SIB/LICR. All rights reserved
 *
 * SIB, Swiss Institute of Bioinformatics
 * Ludwig Institute for Cancer Research (LICR)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/LICR nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/LICR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package newance.psmcombiner;

import newance.psmconverter.PeptideMatchData;
import newance.util.NewAnceParams;

import java.io.*;
import java.util.*;

/**
 * @author Markus Müller
 */

public class CometScoreHistogram extends SmoothedScoreHistogram {

    protected static List<List<Integer>> nnIndexMap = null;

    protected final double minXCorr;
    protected final double maxXCorr;
    protected final int nrXCorrBins;
    protected final double minDeltaCn;
    protected final double maxDeltaCn;
    protected final int nrDeltaCnBins;
    protected final double minSpScore;
    protected final double maxSpScore;
    protected final int nrSpScoreBins;
    protected final double xCorrBinWidth;
    protected final double deltaCnBinWidth;
    protected final double spScoreBinWidth;
    protected final List<Float> xCorrMids;
    protected final List<Float> deltaCnMids;
    protected final List<Float> spScoreMids;

    public CometScoreHistogram(int[] nrBins) {
        super(nrBins);

        this.minXCorr = NewAnceParams.getInstance().getMinXCorr();
        this.maxXCorr = NewAnceParams.getInstance().getMaxXCorr();
        this.nrXCorrBins = NewAnceParams.getInstance().getNrXCorrBins();
        this.minDeltaCn = NewAnceParams.getInstance().getMinDeltaCn();
        this.maxDeltaCn = NewAnceParams.getInstance().getMaxDeltaCn();
        this.nrDeltaCnBins = NewAnceParams.getInstance().getNrDeltaCnBins();
        this.minSpScore = NewAnceParams.getInstance().getMinSpScore();
        this.maxSpScore = NewAnceParams.getInstance().getMaxSpScore();
        this.nrSpScoreBins = NewAnceParams.getInstance().getNrSpScoreBins();

        this.xCorrBinWidth = (maxXCorr-minXCorr)/nrXCorrBins;
        this.deltaCnBinWidth = (maxDeltaCn-minDeltaCn)/nrDeltaCnBins;
        this.spScoreBinWidth = (maxSpScore-minSpScore)/nrSpScoreBins;

        this.xCorrMids = calcMids(calcBreaks((float)minXCorr,(float)maxXCorr,nrXCorrBins));
        this.deltaCnMids = calcMids(calcBreaks((float)minDeltaCn,(float)maxDeltaCn,nrDeltaCnBins));
        this.spScoreMids = calcMids(calcBreaks((float)minSpScore,(float)maxSpScore,nrSpScoreBins));

        if (nnIndexMap==null) nnIndexMap = initNNIndexMap();
        this.smoothedHistogram = null;
    }

    public CometScoreHistogram(int[] nrBins, double minXCorr, double maxXCorr, int nrXCorrBins,
                               double minDeltaCn, double maxDeltaCn, int nrDeltaCnBins,
                               double minSpScore, double maxSpScore, int nrSpScoreBins) {

        super(nrBins);

        this.minXCorr = minXCorr;
        this.maxXCorr = maxXCorr;
        this.nrXCorrBins = nrXCorrBins;
        this.minDeltaCn = minDeltaCn;
        this.maxDeltaCn = maxDeltaCn;
        this.nrDeltaCnBins = nrDeltaCnBins;
        this.minSpScore = minSpScore;
        this.maxSpScore = maxSpScore;
        this.nrSpScoreBins = nrSpScoreBins;

        this.xCorrBinWidth = (maxXCorr-minXCorr)/nrXCorrBins;
        this.deltaCnBinWidth = (maxDeltaCn-minDeltaCn)/nrDeltaCnBins;
        this.spScoreBinWidth = (maxSpScore-minSpScore)/nrSpScoreBins;

        this.xCorrMids = calcMids(calcBreaks((float)minXCorr,(float)maxXCorr,nrXCorrBins));
        this.deltaCnMids = calcMids(calcBreaks((float)minDeltaCn,(float)maxDeltaCn,nrDeltaCnBins));
        this.spScoreMids = calcMids(calcBreaks((float)minSpScore,(float)maxSpScore,nrSpScoreBins));

        if (nnIndexMap==null) nnIndexMap = initNNIndexMap();
        this.smoothedHistogram = null;
    }



    public CometScoreHistogram(ScoreHistogram cometScoreHistogram) {

        super(cometScoreHistogram);

        this.minXCorr = NewAnceParams.getInstance().getMinXCorr();
        this.maxXCorr = NewAnceParams.getInstance().getMaxXCorr();
        this.nrXCorrBins = NewAnceParams.getInstance().getNrXCorrBins();
        this.minDeltaCn = NewAnceParams.getInstance().getMinDeltaCn();
        this.maxDeltaCn = NewAnceParams.getInstance().getMaxDeltaCn();
        this.nrDeltaCnBins = NewAnceParams.getInstance().getNrDeltaCnBins();
        this.minSpScore = NewAnceParams.getInstance().getMinSpScore();
        this.maxSpScore = NewAnceParams.getInstance().getMaxSpScore();
        this.nrSpScoreBins = NewAnceParams.getInstance().getNrSpScoreBins();

        this.xCorrBinWidth = (maxXCorr-minXCorr)/nrXCorrBins;
        this.deltaCnBinWidth = (maxDeltaCn-minDeltaCn)/nrDeltaCnBins;
        this.spScoreBinWidth = (maxSpScore-minSpScore)/nrSpScoreBins;

        this.xCorrMids = calcMids(calcBreaks((float)minXCorr,(float)maxXCorr,nrXCorrBins));
        this.deltaCnMids = calcMids(calcBreaks((float)minDeltaCn,(float)maxDeltaCn,nrDeltaCnBins));
        this.spScoreMids = calcMids(calcBreaks((float)minSpScore,(float)maxSpScore,nrSpScoreBins));

        if (nnIndexMap==null) nnIndexMap = initNNIndexMap();
        this.smoothedHistogram = null;
    }

    @Override
    protected int index(PeptideMatchData peptideMatchData) {

        int xcorrIdx = get1DIndex(peptideMatchData.getScore("xcorr"), minXCorr, xCorrBinWidth, nrXCorrBins-1);
        int deltacnIdx = get1DIndex(peptideMatchData.getScore("deltacn"), minDeltaCn, deltaCnBinWidth, nrDeltaCnBins-1);
        int spscoreIdx = get1DIndex(peptideMatchData.getScore("spscore"), minSpScore, spScoreBinWidth, nrSpScoreBins-1);

        int index = nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx) + xcorrIdx;

        return index;
    }

    protected int index(double xCorr, double deltaCn, double spScore) {

        int xcorrIdx = get1DIndex(xCorr, minXCorr, xCorrBinWidth, nrXCorrBins-1);
        int deltacnIdx = get1DIndex(deltaCn, minDeltaCn, deltaCnBinWidth, nrDeltaCnBins-1);
        int spscoreIdx = get1DIndex(spScore, minSpScore, spScoreBinWidth, nrSpScoreBins-1);

        int index = nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx) + xcorrIdx;

        return index;
    }

    public void add(double xCorr, double deltaCn, double spScore, float value, boolean isDecoy) {

        int bin = index(xCorr,deltaCn,spScore);

        int idx = indexMap.get(bin);
        if (idx<0) {
            indexMap.set(bin,currIndex);
            psmBins.add(bin);
            currIndex++;
        }

        if (idx < 0) { // new bin
            if (!isDecoy) targetCnts.add(value);
            else decoyCnts.add(value);
        } else { // already recorded bin
            if (!isDecoy) targetCnts.set(idx,targetCnts.get(idx)+value);
            else decoyCnts.set(idx,decoyCnts.get(idx)+value);
        }

        if (isDecoy) totTargetCnt += value;
        else totDecoyCnt += value;
    }

    @Override
    protected Set<Integer> getNeighbourIndex(int bin) {

        List<Integer> scoreIdx = nnIndexMap.get(bin);

        int xcorrIdx = scoreIdx.get(0);
        int deltacnIdx = scoreIdx.get(1);
        int spscoreIdx = scoreIdx.get(2);

        Set<Integer> neighbours = new HashSet<>();

        if (xcorrIdx+1<nrXCorrBins) neighbours.add(nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx) + xcorrIdx+1);
        if (xcorrIdx-1>=0) neighbours.add(nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx) + xcorrIdx-1);
        if (deltacnIdx+1<nrDeltaCnBins) neighbours.add(nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx+1) + xcorrIdx);
        if (deltacnIdx-1>=0) neighbours.add(nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx-1) + xcorrIdx);
        if (spscoreIdx+1<nrSpScoreBins) neighbours.add(nrXCorrBins*(nrDeltaCnBins*(spscoreIdx+1) + deltacnIdx) + xcorrIdx);
        if (spscoreIdx-1>=0) neighbours.add(nrXCorrBins*(nrDeltaCnBins*(spscoreIdx-1) + deltacnIdx) + xcorrIdx);

        return neighbours;
    }


    protected List<List<Integer>> initNNIndexMap(){

        List<List<Integer>> indexMap = new ArrayList<>(totNrBins);
        for (int i=0;i<totNrBins;i++) indexMap.add(new ArrayList<>(3));

        for (int spscoreIdx = 0; spscoreIdx < nrSpScoreBins; spscoreIdx++) {
            for (int deltacnIdx = 0; deltacnIdx < nrDeltaCnBins; deltacnIdx++) {
                for (int xcorrIdx = 0; xcorrIdx < nrXCorrBins; xcorrIdx++) {
                    int index = nrXCorrBins*(nrDeltaCnBins*spscoreIdx + deltacnIdx) + xcorrIdx;

                    List<Integer> indexes = indexMap.get(index);
                    indexes.add(xcorrIdx);
                    indexes.add(deltacnIdx);
                    indexes.add(spscoreIdx);
                }
            }
        }
        return indexMap;
    }


    public void write(File outputFile) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write("#"+String.format("%.3f\t%.3f\t%d\t%.3f\t%.3f\t%d\t%.3f\t%.3f\t%d\n",
                    minXCorr,maxXCorr,nrXCorrBins,minDeltaCn,maxDeltaCn,nrDeltaCnBins,minSpScore,maxSpScore,nrSpScoreBins));
            writer.write("XCorr\tDeltaCn\tSpScore\tValue\tType\n");

            for (int i=0;i<indexMap.size();i++) {

                List<Float> mids = getMids(i);
                String coords = "";
                for (Float mid : mids) coords+= String.format("%.3f",mid)+"\t";

                float value = 0f;
                if (indexMap.get(i)>=0) {
                    int idx = indexMap.get(i);
                    writer.write(coords+String.format("%.3f",targetCnts.get(idx))+"\ttarget\n");
                    writer.write(coords+String.format("%.3f",decoyCnts.get(idx))+"\tdecoy\n");
                    if (!lFDR.isEmpty()) value = lFDR.get(idx); // check whether lFDR is already calculated
                    writer.write(coords+String.format("%.3f",value)+"\tlFDR\n");
                } else {
                    writer.write(coords+String.format("%.3f",value)+"\ttarget\n");
                    writer.write(coords+String.format("%.3f",value)+"\tdecoy\n");
                    writer.write(coords+String.format("%.3f",value)+"\tlFDR\n");
                }
            }

            writer.close();
        } catch (IOException e) {

        }
    }


    public static CometScoreHistogram read(File inputFile) {

        CometScoreHistogram cometScoreHistogram = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String line = reader.readLine();

            if (!line.startsWith("#")) {
                System.out.println("Ivalid histogram input file "+inputFile+". First line must start with #.");
                return null;
            }

            line = line.substring(1);
            String[] fields = line.split("\t");
            double minXCorr = Double.parseDouble(fields[0]);
            double maxXCorr = Double.parseDouble(fields[1]);
            int nrXCorrBins = Integer.parseInt(fields[2]);
            double minDeltaCn = Double.parseDouble(fields[3]);
            double maxDeltaCn = Double.parseDouble(fields[4]);
            int nrDeltaCnBins = Integer.parseInt(fields[5]);
            double minSpScore = Double.parseDouble(fields[6]);
            double maxSpScore = Double.parseDouble(fields[7]);
            int nrSpScoreBins = Integer.parseInt(fields[8]);

            int[] nrBins = new int[3];
            nrBins[0] = nrXCorrBins;
            nrBins[1] = nrDeltaCnBins;
            nrBins[2] = nrSpScoreBins;

            cometScoreHistogram = new CometScoreHistogram(nrBins, minXCorr, maxXCorr, nrXCorrBins,
                    minDeltaCn, maxDeltaCn, nrDeltaCnBins, minSpScore, maxSpScore, nrSpScoreBins);

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                fields = line.split("\t");

                double xCorr = Double.parseDouble(fields[0]);
                double deltaCn = Double.parseDouble(fields[1]);
                double spScore = Double.parseDouble(fields[2]);
                float value = Float.parseFloat(fields[3]);
                boolean isDecoy = fields[4].equals("decoy");

                cometScoreHistogram.add(xCorr, deltaCn, spScore, value, isDecoy);
            }


            reader.close();
        } catch (IOException e) {

        }

        return cometScoreHistogram;
    }

    protected List<Float> getMids(int bin) {

        int xCorrIdx = bin%nrXCorrBins;
        bin = (bin-xCorrIdx)/nrXCorrBins;
        int deltaCnIdx = bin%nrDeltaCnBins;
        bin = (bin-deltaCnIdx)/nrDeltaCnBins;
        int spScoreIdx = bin%nrSpScoreBins;

        return Arrays.asList(new Float[]{xCorrMids.get(xCorrIdx),deltaCnMids.get(deltaCnIdx),spScoreMids.get(spScoreIdx)});
    }

}
