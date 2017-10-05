package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.exceptions.AppBootException;

public interface AppBoot {
    void init() throws AppBootException;
}
