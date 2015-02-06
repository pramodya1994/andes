/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.andes.kernel.distruptor.inbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.andes.kernel.AndesException;
import org.wso2.andes.kernel.OnflightMessageTracker;

import java.util.UUID;

import static org.wso2.andes.kernel.distruptor.inbound.AndesInboundStateEvent.StateEvent.CHANNEL_CLOSE_EVENT;
import static org.wso2.andes.kernel.distruptor.inbound.AndesInboundStateEvent.StateEvent.CHANNEL_OPEN_EVENT;

/**
 * Andes channel related events are published to Disruptor as InboundAndesChannelEvent
 */
public class InboundAndesChannelEvent implements AndesInboundStateEvent {

    private static Log log = LogFactory.getLog(InboundAndesChannelEvent.class);
    
    /**
     * Channel event type handle by the event object 
     */
    private StateEvent eventType;

    /**
     * Channel ID 
     */
    private UUID channelID;
    
    public InboundAndesChannelEvent( UUID channelID) {
        this.channelID = channelID;
    }
    
    @Override
    public void updateState() throws AndesException {
        switch (eventType) {
            case CHANNEL_OPEN_EVENT:
                OnflightMessageTracker.getInstance().addNewChannelForTracking(channelID);
                break;
            case CHANNEL_CLOSE_EVENT:
                OnflightMessageTracker.getInstance().releaseAllMessagesOfChannelFromTracking(channelID);
                break;
            default:
                log.error("Event type not set properly " + eventType);
                break;
        }
    }

    @Override
    public StateEvent getEventType() {
        return eventType;
    }

    /**
     * Update event to a channel open event 
     */
    public void prepareForChannelOpen() {
        eventType = CHANNEL_OPEN_EVENT;
    }

    /**
     * Update event to a channel close event 
     */
    public void prepareForChannelClose() {
        eventType = CHANNEL_CLOSE_EVENT;
    }
}