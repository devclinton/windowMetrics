package com.devclinton.WindowMetrics.models;

/**
 * Window Metric holds an entry that describes the application, title, checksum
 * and additional metrics associated with an active window
 * <p>
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "window_metric")
@NamedQueries({
        @NamedQuery(name = "WindowMetric.findAll", query = "SELECT wm FROM WindowMetric wm"),
        @NamedQuery(name = "WindowMetric.findAllLessThanDate", query = "SELECT wm FROM WindowMetric wm where updated < :date")
})
public class WindowMetric extends AbstractTimestampEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "process_id", referencedColumnName = "id")
    @ManyToOne
    private ProcessMetric process;

    @NotNull
    @Size(max = 2048)
    @Column(name = "window_title")
    private String windowTitle;

    @NotNull
    @Column(name = "active_seconds")
    private int activeSeconds;

    public WindowMetric() {
    }

    public WindowMetric(String title, int activeSeconds) {
        this.windowTitle = title;
        this.activeSeconds = activeSeconds;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public int getActiveSeconds() {
        return activeSeconds;
    }

    public void setActiveSeconds(int activeSeconds) {
        this.activeSeconds = activeSeconds;
    }

    public ProcessMetric getProcess() {
        return process;
    }

    public void setProcess(ProcessMetric process) {
        this.process = process;
    }
}
