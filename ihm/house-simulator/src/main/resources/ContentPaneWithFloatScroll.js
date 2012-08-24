/*
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
Echo.ContentPaneWithFloatScroll = Core.extend(Echo.Component, {

    $static: {
    
        /**
         * Setting for <code>overflow</code> property that scrollbars should be displayed when content overflows.
         * @type Number
         */
        OVERFLOW_AUTO: 0,

        /** 
         * Setting for <code>overflow</code> property indicating that overflowing content should be hidden.
         * @type Number 
         */
        OVERFLOW_HIDDEN: 1,

        /** 
         * Setting for <code>overflow</code> property indicating that scrollbars should always be displayed.
         * @type Number 
         */
        OVERFLOW_SCROLL: 2
    },

    $load: function() {
        Echo.ComponentFactory.registerType("ContentPaneWithFloatScroll", this);
        Echo.ComponentFactory.registerType("CPWFS", this);
    },

    /** @see Echo.Component#componentType */
    componentType: "ContentPaneWithFloatScroll",
    
    /** @see Echo.Component#pane */
    pane: true
});