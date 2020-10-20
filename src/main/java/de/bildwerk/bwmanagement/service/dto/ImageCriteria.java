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
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link de.bildwerk.bwmanagement.domain.Image} entity. This class is used
 * in {@link de.bildwerk.bwmanagement.web.rest.ImageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /images?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ImageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter uploadDate;

    public ImageCriteria() {
    }

    public ImageCriteria(ImageCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.uploadDate = other.uploadDate == null ? null : other.uploadDate.copy();
    }

    @Override
    public ImageCriteria copy() {
        return new ImageCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateFilter uploadDate) {
        this.uploadDate = uploadDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImageCriteria that = (ImageCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(uploadDate, that.uploadDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        uploadDate
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ImageCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (uploadDate != null ? "uploadDate=" + uploadDate + ", " : "") +
            "}";
    }

}
