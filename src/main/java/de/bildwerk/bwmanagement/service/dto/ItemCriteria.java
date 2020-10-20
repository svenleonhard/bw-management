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
 * Criteria class for the {@link de.bildwerk.bwmanagement.domain.Item} entity. This class is used
 * in {@link de.bildwerk.bwmanagement.web.rest.ItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter qrCode;

    private StringFilter description;

    private LongFilter pictureId;

    private LongFilter contentId;

    private LongFilter lettingId;

    public ItemCriteria() {
    }

    public ItemCriteria(ItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.qrCode = other.qrCode == null ? null : other.qrCode.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.pictureId = other.pictureId == null ? null : other.pictureId.copy();
        this.contentId = other.contentId == null ? null : other.contentId.copy();
        this.lettingId = other.lettingId == null ? null : other.lettingId.copy();
    }

    @Override
    public ItemCriteria copy() {
        return new ItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getQrCode() {
        return qrCode;
    }

    public void setQrCode(IntegerFilter qrCode) {
        this.qrCode = qrCode;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getPictureId() {
        return pictureId;
    }

    public void setPictureId(LongFilter pictureId) {
        this.pictureId = pictureId;
    }

    public LongFilter getContentId() {
        return contentId;
    }

    public void setContentId(LongFilter contentId) {
        this.contentId = contentId;
    }

    public LongFilter getLettingId() {
        return lettingId;
    }

    public void setLettingId(LongFilter lettingId) {
        this.lettingId = lettingId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ItemCriteria that = (ItemCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(qrCode, that.qrCode) &&
            Objects.equals(description, that.description) &&
            Objects.equals(pictureId, that.pictureId) &&
            Objects.equals(contentId, that.contentId) &&
            Objects.equals(lettingId, that.lettingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        qrCode,
        description,
        pictureId,
        contentId,
        lettingId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ItemCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (qrCode != null ? "qrCode=" + qrCode + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (pictureId != null ? "pictureId=" + pictureId + ", " : "") +
                (contentId != null ? "contentId=" + contentId + ", " : "") +
                (lettingId != null ? "lettingId=" + lettingId + ", " : "") +
            "}";
    }

}
