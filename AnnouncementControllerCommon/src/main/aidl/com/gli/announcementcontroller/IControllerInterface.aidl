// IControllerInterface.aidl
package com.gli.announcementcontroller;

import com.gli.announcementcontroller.IMediaInterface;
// Declare any non-default types here with import statements

interface IControllerInterface {

    boolean show(int timeout);
    boolean hide();
    int initialize(IMediaInterface mediainterface);
}
