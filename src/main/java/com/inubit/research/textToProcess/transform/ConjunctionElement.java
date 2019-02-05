/**
 * copyright
 * Inubit AG
 * Schoeneberger Ufer 89
 * 10785 Berlin
 * Germany
 */
package com.inubit.research.textToProcess.transform;

import com.inubit.research.textToProcess.worldModel.SpecifiedElement;

/**
 * @author ff
 */
public class ConjunctionElement {

    private SpecifiedElement f_to;
    private SpecifiedElement f_from;
    private ConjunctionType f_type;

    /**
     *
     */
    public ConjunctionElement(SpecifiedElement from, SpecifiedElement to, ConjunctionType type) {
        setFrom(from);
        setTo(to);
        setType(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConjunctionElement) {
            ConjunctionElement _link = (ConjunctionElement) obj;
            return _link.getFrom().getWordIndex() == this.getFrom().getWordIndex()
                    && _link.getTo().getWordIndex() == this.getTo().getWordIndex() &&
                    _link.getType().equals(this.getType());
        }
        return false;
    }

    public SpecifiedElement getTo() {
        return f_to;
    }

    public void setTo(SpecifiedElement f_to) {
        this.f_to = f_to;
    }

    public SpecifiedElement getFrom() {
        return f_from;
    }

    public void setFrom(SpecifiedElement f_from) {
        this.f_from = f_from;
    }

    public ConjunctionType getType() {
        return f_type;
    }

    public void setType(ConjunctionType f_type) {
        this.f_type = f_type;
    }

    @Override
    public String toString() {
        return f_to.toString() + "-" + f_type + "-" + f_from.toString();
    }

    public enum ConjunctionType {
        AND,
        OR,
        ANDOR,
        MIXED
    }

}
