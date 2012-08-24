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
/**
 * Button component: a stateless "push" button which is used to initiate an
 * action.  May not contain child components.
 */
Echo.FloatingButton = Core.extend(Echo.AbstractButton, {

    $load: function() {
        Echo.ComponentFactory.registerType("FloatingButton", this);
        Echo.ComponentFactory.registerType("FB", this);
    },
    
    /**
     * Render as floating pane in ContentPanes. 
     * @see Echo.ContentPane 
     */
    floatingPane: true,

    /** @see Echo.Component#componentType */
    componentType: "FloatingButton"
});