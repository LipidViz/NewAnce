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
package newance.mzjava.ms.io.mgf;

import com.google.common.base.Optional;
import newance.mzjava.ms.peaklist.PeakList;
import newance.mzjava.ms.spectrum.MsnSpectrum;

import java.text.NumberFormat;

/**
* @author Oliver Horlacher
* @version sqrt -1
*/
public class MgfParameters implements AbstractMgfWriter.Parameters {

    private final NumberFormat mzFormat;
    private final NumberFormat intensityFormat;
    private static final NumberFormat RT_NUMBER_FORMAT = NumberFormatFactory.valueOf(2);

    private Optional<MsnSpectrum> headerData = Optional.absent();

    public MgfParameters(PeakList.Precision precision) {

        this(PeakListPrecisionFormat.getMzFormat(precision), PeakListPrecisionFormat.getIntensityFormat(precision),
                Optional.<MsnSpectrum>absent());
    }

    public MgfParameters(PeakList.Precision precision, MsnSpectrum headerData) {

        this(PeakListPrecisionFormat.getMzFormat(precision), PeakListPrecisionFormat.getIntensityFormat(precision),
                Optional.<MsnSpectrum>fromNullable(headerData));
    }

    public MgfParameters(NumberFormat mzFormat, NumberFormat intensityFormat, Optional<MsnSpectrum> headerData) {

        this.mzFormat = mzFormat;
        this.intensityFormat = intensityFormat;
        this.headerData = headerData;
    }

    @Override
    public NumberFormat getMzFormat() {

        return mzFormat;
    }

    @Override
    public NumberFormat getIntensityFormat() {

        return intensityFormat;
    }

    @Override
    public NumberFormat getRetentionTimeFormat() {

        return RT_NUMBER_FORMAT;
    }

    public Optional<MsnSpectrum> getHeaderData() {

        return headerData;
    }
}
