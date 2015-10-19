package com.devclinton.WindowMetrics.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clinton Collins <clinton.collins@cgi.com> on 10/16/15.
 */
@Entity
@Table(name = "process_metric")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Process.findByIdCkSum", query = "SELECT wm FROM ProcessMetric wm where executable = :exe AND checksum = :cksum"),
        @NamedQuery(name = "Process.findAll", query = "SELECT wm FROM ProcessMetric wm"),
        @NamedQuery(name = "Process.findAllLessThanDate", query = "SELECT wm FROM ProcessMetric wm where updated < :date")
})
public class ProcessMetric extends AbstractTimestampEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 2048)
    @Column(name = "executable")
    private String executable;

    @Size(max = 32)
    @Column(name = "checksum")
    private String checkSum;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "process")
    @OrderBy("updated")
    private List<WindowMetric> windows = new ArrayList<WindowMetric>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "process")
    @OrderBy("updated")
    private List<ArgumentsMetric> arguments = new ArrayList<ArgumentsMetric>();

    public List<WindowMetric> getWindows() {
        return windows;
    }

    public void setWindows(List<WindowMetric> windows) {
        this.windows = windows;
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

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public List<ArgumentsMetric> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArgumentsMetric> arguments) {
        this.arguments = arguments;
    }
}
