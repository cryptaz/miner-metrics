package org.cryptaz.minermetrics.api;


import org.cryptaz.minermetrics.models.dto.ClaymoreTickDTO;

public interface MinerAPI {
    ClaymoreTickDTO tick();
    String getUrl();
}
