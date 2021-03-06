/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package newance.mzjava.ms.peaklist;


import java.util.Arrays;

/**
 * PeakList that stores the m/z values as doubles and the intensities as a constant double value
 *
 * @author Oliver Horlacher
 * @version 1.0
 */
public class DoubleConstantPeakList<A extends PeakAnnotation> extends AbstractPeakList<A> {

    private double[] mzList;
    private final double intensity;

    /**
     * Create an clear peak list
     *
     * @param intensity the intensity of all peaks in this PeakList
     */
    public DoubleConstantPeakList(double intensity) {

        this(intensity, 0);
    }

    /**
     * Create a peak list that has a capacity set to initialCapacity
     *
     * @param intensity       the intensity of all peaks in this PeakList
     * @param initialCapacity the initial capacity of the peak list
     */
    public DoubleConstantPeakList(double intensity, int initialCapacity) {

        this.intensity = intensity;
        mzList = new double[initialCapacity];
    }

    private DoubleConstantPeakList(DoubleConstantPeakList<A> src){

        super(src);

        this.intensity = src.intensity;
        mzList = new double[size];
    }

    protected DoubleConstantPeakList(DoubleConstantPeakList<A> src, PeakProcessor<A,A> peakProcessor) {

        this(src);

        apply(src,this,peakProcessor);

    }

    protected DoubleConstantPeakList(DoubleConstantPeakList<A> src, PeakProcessorChain<A> peakProcessorChain) {

        this(src);

        apply(src,this,peakProcessorChain);
    }

    public double getIntensity() {

        return intensity;
    }

    @Override
    public double getTotalIonCurrent() {

        return intensity * size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMz(int index) {

        rangeCheck(index);

        return mzList[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getIntensity(int index) {

        rangeCheck(index);

        return intensity;
    }

    /**
     * Add a mz to this peak list.  The intensity value is ignored
     *
     * @param mz            the mz to add
     * @param voidIntensity the intensity to add
     */
    @Override
    public int doAppend(double mz, double voidIntensity) {

        if (size > 0 && mz == mzList[size - 1]) {

            return size - 1;
        }
        if (size > 0 && mz < mzList[size - 1]) {

            throw new IllegalStateException("the added peak is not sorted.  Adding " + mz + " to " + mzList[size - 1]);
        }

        mzList[size] = mz;
        totalIonCurrent += intensity;

        size++;
        return size - 1;
    }

    @Override
    protected int doInsert(double mz, double intensity) {

        int indexOf = indexOf(0, size, mz);
        if (indexOf < 0) {

            indexOf = -1 * (indexOf + 1);

            PrimitiveArrayUtils.insert(mz, indexOf, mzList, size);
            size++;
            shiftAnnotations(indexOf);
        }

        totalIonCurrent += this.intensity;
        return indexOf;
    }

    @Override
    protected PeakSink<A> newMergeSink() {

        return new MergePeakSink<>(this);
    }

    /**
     * Add a mz to this peak list.
     *
     * @param mz the mz to add
     */
    public void add(double mz) {

        add(mz, 0);
    }

    /**
     * Trims the capacity of this <tt>DoublePeakList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of a <tt>DoublePeakList</tt> instance.
     */
    @Override
    public void trimToSize() {

        mzList = PrimitiveArrayUtils.trim(mzList, size);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, double mzKey) {

        if (size == 0) {

            return -1;
        }

        return Arrays.binarySearch(mzList, Math.max(0, fromIndex), Math.min(size, toIndex), mzKey);
    }

    /**
     * Returns the index of the most intense peak in the range minMz to maxMz.  If there are no peaks in this range -1
     * returned.
     *
     * @param minMz the minMz
     * @param maxMz the maxMz
     * @return the index of the most intense peak in the range minMz to maxMz.  If there are no peaks in this range -1
     * returned
     */
    @Override
    public int getMostIntenseIndex(double minMz, double maxMz) {

        if (minMz > maxMz)
            throw new IllegalArgumentException("Min mz needs to be smaller than max mz.  minMz = " + minMz + " maxMz " + maxMz);

        final int index = indexEqualOrLarger(minMz, -1, mzList);
        return index < size ? index : -1;
    }

    @Override
    public int getMostIntenseIndex() {

        return size == 0 ? -1 : 0;
    }

    /**
     * Increases the capacity of this <tt>DoublePeakList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of peaks
     * specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    @Override
    public void ensureCapacity(int minCapacity) {

        if (mzList.length < minCapacity) {

            int remaining = minCapacity - mzList.length;

            mzList = PrimitiveArrayUtils.grow(mzList, remaining);
        }
    }

    @Override
    public Precision getPrecision() {

        return Precision.DOUBLE_CONSTANT;
    }

    @Override
    public DoubleConstantPeakList<A> copy(PeakProcessor<A, A> peakProcessor) {

        return new DoubleConstantPeakList<>(this, peakProcessor);
    }

    @Override
    public DoubleConstantPeakList<A> copy(PeakProcessorChain<A> peakProcessorChain) {

        return new DoubleConstantPeakList<>(this, peakProcessorChain);
    }

    @Override
    public double calcVectorLength() {

        return Math.sqrt(size * Math.pow(intensity, 2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getMzs(double[] dest) {

        return PrimitiveArrayUtils.copyArray(mzList, 0, dest, 0, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getMzs(double[] dest, int destPos) {

        return PrimitiveArrayUtils.copyArray(mzList, 0, dest, destPos, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getMzs(int srcPos, double[] dest, int destPos, int length) {

        return PrimitiveArrayUtils.copyArray(mzList, srcPos, dest, destPos, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getIntensities(double[] dest) {

        return getIntensities(0, dest, 0, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getIntensities(double[] dest, int destPos) {

        return getIntensities(0, dest, destPos, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getIntensities(int srcPos, double[] dest, int destPos, int length) {

        if (dest == null) dest = new double[destPos + length];

        Arrays.fill(dest, destPos, destPos + length, intensity);

        return dest;
    }

    /**
     * Replace the intensity at index with intensity
     *
     * @param intensity the new intensity
     * @param index     the index for which the intensity is to be set
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public void setIntensityAt(double intensity, int index) {

        throw new UnsupportedOperationException("DoubleConstantPeakList has a constant intensity which is immutable");
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DoubleConstantPeakList that = (DoubleConstantPeakList) o;

        return Double.compare(that.intensity, intensity) == 0
                && PrimitiveArrayUtils.arrayEquals(mzList, that.mzList, size);
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();
        long temp;

        result = 31 * result + PrimitiveArrayUtils.arrayHashCode(mzList, size);
        temp = intensity != +0.0d ? Double.doubleToLongBits(intensity) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * PeakSink that is used to merge two peak lists
     */
    private static class MergePeakSink<A extends PeakAnnotation> extends AbstractMergePeakSink<A, DoubleConstantPeakList<A>> {

        private double[] tmpMzList;

        private MergePeakSink(DoubleConstantPeakList<A> peakList) {

            super(peakList);
        }

        @Override
        protected void addIntensity(double intensity, int index) {

            //Constant peak lists do not have any intensity
        }

        @Override
        public void start(int size) {

            tmpMzList = new double[size];
        }

        protected void addToArray(double mz, double intensity, int index) {

            tmpMzList[index] = mz;
        }

        @Override
        protected double getMz(int index) {

            return tmpMzList[index];
        }

        protected void setArrays() {

            peakList.mzList = tmpMzList;
            peakList.totalIonCurrent = peakList.intensity * peakList.size();
        }
    }
}
