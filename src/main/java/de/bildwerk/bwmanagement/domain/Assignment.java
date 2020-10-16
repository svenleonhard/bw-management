package de.bildwerk.bwmanagement.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A Assignment.
 */
@Entity
@Table(name = "assignment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Assignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "comment")
    private String comment;

    @OneToOne
    @JoinColumn(unique = true)
    private Item boxItem;

    @OneToOne
    @JoinColumn(unique = true)
    private Item box;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public Assignment description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public Assignment comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Item getBoxItem() {
        return boxItem;
    }

    public Assignment boxItem(Item item) {
        this.boxItem = item;
        return this;
    }

    public void setBoxItem(Item item) {
        this.boxItem = item;
    }

    public Item getBox() {
        return box;
    }

    public Assignment box(Item item) {
        this.box = item;
        return this;
    }

    public void setBox(Item item) {
        this.box = item;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assignment)) {
            return false;
        }
        return id != null && id.equals(((Assignment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assignment{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
