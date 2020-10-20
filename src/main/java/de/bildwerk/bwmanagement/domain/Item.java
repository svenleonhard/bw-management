package de.bildwerk.bwmanagement.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Item.
 */
@Entity
@Table(name = "item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "qr_code", nullable = false)
    private Integer qrCode;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne
    @JoinColumn(unique = true)
    private Image picture;

    @OneToMany(mappedBy = "item")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Content> contents = new HashSet<>();

    @OneToMany(mappedBy = "item")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Letting> lettings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQrCode() {
        return qrCode;
    }

    public Item qrCode(Integer qrCode) {
        this.qrCode = qrCode;
        return this;
    }

    public void setQrCode(Integer qrCode) {
        this.qrCode = qrCode;
    }

    public String getDescription() {
        return description;
    }

    public Item description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getPicture() {
        return picture;
    }

    public Item picture(Image image) {
        this.picture = image;
        return this;
    }

    public void setPicture(Image image) {
        this.picture = image;
    }

    public Set<Content> getContents() {
        return contents;
    }

    public Item contents(Set<Content> contents) {
        this.contents = contents;
        return this;
    }

    public Item addContent(Content content) {
        this.contents.add(content);
        content.setItem(this);
        return this;
    }

    public Item removeContent(Content content) {
        this.contents.remove(content);
        content.setItem(null);
        return this;
    }

    public void setContents(Set<Content> contents) {
        this.contents = contents;
    }

    public Set<Letting> getLettings() {
        return lettings;
    }

    public Item lettings(Set<Letting> lettings) {
        this.lettings = lettings;
        return this;
    }

    public Item addLetting(Letting letting) {
        this.lettings.add(letting);
        letting.setItem(this);
        return this;
    }

    public Item removeLetting(Letting letting) {
        this.lettings.remove(letting);
        letting.setItem(null);
        return this;
    }

    public void setLettings(Set<Letting> lettings) {
        this.lettings = lettings;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        return id != null && id.equals(((Item) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Item{" +
            "id=" + getId() +
            ", qrCode=" + getQrCode() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
