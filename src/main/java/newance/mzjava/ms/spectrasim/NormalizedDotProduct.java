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
package newance.mzjava.ms.spectrasim;

import gnu.trove.list.TDoubleList;

/**
 * Static methods to calculate the normalized dot product
 *
 * @author Oliver Horlacher
 * @version 1.0
 */
public class NormalizedDotProduct {

    private NormalizedDotProduct() {
    }

    public static double cosim(TDoubleList vX, TDoubleList vY) {

        if(vX.isEmpty()) return Double.NaN;

        double aDotB = 0;
        double distA = 0;
        double distB = 0;

        for(int i = 0 ; i < vX.size(); i++) {

            double x = vX.get(i);
            double y = vY.get(i);

            aDotB += x*y;

            distA += Math.pow(x, 2);
            distB += Math.pow(y, 2);
        }

        distA = Math.sqrt(distA);
        distB = Math.sqrt(distB);

        return aDotB/(distA*distB);
    }

    public static double cosim(double[] vX, double[] vY) {

        if (vX.length < 1) return Double.NaN;

        double aDotB = 0;
        double distA = 0;
        double distB = 0;

        for (int i = 0; i < vX.length; i++) {

            double x = vX[i];
            double y = vY[i];

            aDotB += x * y;

            distA += Math.pow(x, 2);
            distB += Math.pow(y, 2);
        }

        distA = Math.sqrt(distA);
        distB = Math.sqrt(distB);

        return aDotB / (distA * distB);
    }
}
