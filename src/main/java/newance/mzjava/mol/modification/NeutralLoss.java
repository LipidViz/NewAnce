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
package newance.mzjava.mol.modification;

import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import newance.mzjava.mol.Atom;
import newance.mzjava.mol.Composition;

import java.util.Collections;
import java.util.Set;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class NeutralLoss extends Composition {

    //Due to unimod this can contain any amino acid and n-term, c-term , protein-n-term and protein-c-term
    private final Set<String> sites;

    public NeutralLoss(TObjectIntMap<Atom> atomCountMap, int charge, Set<String> sites) {

        super(Builder.makeFormula(atomCountMap, charge),
                new TUnmodifiableObjectIntMap<Atom>(new TObjectIntHashMap<Atom>(atomCountMap)), charge);

        this.sites = Collections.unmodifiableSet(sites);
    }

    public Set<String> getSites() {

        return sites;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NeutralLoss that = (NeutralLoss) o;

        return sites.equals(that.sites);
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();
        result = 31 * result + sites.hashCode();
        return result;
    }
}
