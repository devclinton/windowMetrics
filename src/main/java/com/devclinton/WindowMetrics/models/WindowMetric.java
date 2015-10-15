package com.devclinton.WindowMetrics.models;

/**
 * Window Metric holds an entry that describes the application, title, checksum
 * and additional metrics associated with an active window
 * <p>
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "window_metric")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "WindowMetric.findAll", query = "SELECT wm FROM WindowMetric wm"),
        @NamedQuery(name = "WindowMetric.findAllLessThanDate", query = "SELECT wm FROM WindowMetric wm where updated < :date")
})
public class WindowMetric implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 2048)
    @Column(name = "executable")
    private String executable;

    @NotNull
    @Size(max = 2048)
    @Column(name = "window_title")
    private String windowTitle;

    @Size(max = 32)
    @Column(name = "checksum")
    private String checkSum;

    @NotNull
    @Column(name = "active_seconds")
    private int activeSeconds;

    @NotNull
    @Column(name = "created")
    private Date created;

    @NotNull
    @Column(name = "updated")
    private Date updated;

    public WindowMetric() {
    }

    public WindowMetric(String exe, String title, int activeSeconds) {
        this.executable = exe;
        this.windowTitle = title;
        this.activeSeconds = activeSeconds;
    }

    @PrePersist
    protected void onCreate() {
        setCreated(new Date());
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdated(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public int getActiveSeconds() {
        return activeSeconds;
    }

    public void setActiveSeconds(int activeSeconds) {
        this.activeSeconds = activeSeconds;
    }

    public Date getCreated() {
        return created;
    }

    @JsonIgnore
    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    @JsonIgnore
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
