package org.api.regres.DTO;

import java.util.Date;

public class UserJobInfo {
    private String name;
    private String job;
    private Date updatedAt;

    public UserJobInfo(String name, String job, Date updatedAt) {
        this.name = name;
        this.job = job;
        this.updatedAt = updatedAt;
    }

    public UserJobInfo() {
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
