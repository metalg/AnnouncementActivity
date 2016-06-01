// IMediaInterface.aidl
package com.gli.announcementcontroller;

import com.gli.announcementcontroller.TrackInfo;

interface IMediaInterface {

    int getTrackInfoList(out TrackInfo info);
    int setCurrentTrack(in TrackInfo info);
}
