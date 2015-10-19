package com.devclinton.WindowMetrics.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Clinton Collins <clinton.collins@cgi.com> on 10/18/15.
 */
@Entity
@Table(name = "arguments_metrics")
@NamedQueries({
        @NamedQuery(name = "ArgumentsMetric.findAll", query = "SELECT a FROM ArgumentsMetric a"),
        @NamedQuery(name = "ArgumentsMetric.findAllLessThanDate", query = "SELECT a FROM ArgumentsMetric a where updated < :date")
})
public class ArgumentsMetric extends AbstractTimestampEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "arguments")
    private String arguments;

    @JoinColumn(name = "process_id", referencedColumnName = "id")
    @ManyToOne
    private ProcessMetric process;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public ProcessMetric getProcess() {
        return process;
    }

    public void setProcess(ProcessMetric process) {
        this.process = process;
    }
}
