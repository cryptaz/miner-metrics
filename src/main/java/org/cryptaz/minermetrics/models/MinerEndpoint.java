package org.cryptaz.minermetrics.models;

import javax.persistence.*;

@Entity
@Table(name = "miner_endpoints")
public class MinerEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MinerEndpoint{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
