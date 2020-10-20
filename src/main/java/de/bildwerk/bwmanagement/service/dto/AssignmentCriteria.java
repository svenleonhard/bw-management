package de.bildwerk.bwmanagement.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link de.bildwerk.bwmanagement.domain.Assignment} entity. This class is used
 * in {@link de.bildwerk.bwmanagement.web.rest.AssignmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /assignments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AssignmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private StringFilter comment;

    private LongFilter boxItemId;

    private LongFilter boxId;

    public AssignmentCriteria() {
    }

    public AssignmentCriteria(AssignmentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.comment = other.comment == null ? null : other.comment.copy();
        this.boxItemId = other.boxItemId == null ? null : other.boxItemId.copy();
        this.boxId = other.boxId == null ? null : other.boxId.copy();
    }

    @Override
    public AssignmentCriteria copy() {
        return new AssignmentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getComment() {
        return comment;
    }

    public void setComment(StringFilter comment) {
        this.comment = comment;
    }

    public LongFilter getBoxItemId() {
        return boxItemId;
    }

    public void setBoxItemId(LongFilter boxItemId) {
        this.boxItemId = boxItemId;
    }

    public LongFilter getBoxId() {
        return boxId;
    }

    public void setBoxId(LongFilter boxId) {
        this.boxId = boxId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AssignmentCriteria that = (AssignmentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(comment, that.comment) &&
            Objects.equals(boxItemId, that.boxItemId) &&
            Objects.equals(boxId, that.boxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        description,
        comment,
        boxItemId,
        boxId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignmentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (comment != null ? "comment=" + comment + ", " : "") +
                (boxItemId != null ? "boxItemId=" + boxItemId + ", " : "") +
                (boxId != null ? "boxId=" + boxId + ", " : "") +
            "}";
    }

}
