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
 * Drag source component.
 * 
 * @cp {Array} dropTargetIds array of strings specifying renderIds of valid drop target components
 */
Echo.FloatingButtonDragSource = Core.extend(Echo.Component, {
    
    $load: function() {
        Echo.ComponentFactory.registerType("Echo.FloatingButtonDragSource", this);
    },
    
    /** @see Echo.Component#componentType */
    componentType: "Echo.FloatingButtonDragSource",

    /**
     * Programmatically performs a drop action.
     * 
     * @param {String} dropTarget the renderId of the valid drop target component on which the source component was dropped
     * @param {String} specificTarget the renderId of the most-specific component on which the source component was dropped 
     *        (must be a descendant of dropTargetComponent, may be equal to dropTarget)
     */
    doDrop: function(dropTarget, specificTarget, targetX, targetY) {
        this.fireEvent({ type: "drop", source: this, dropTarget: dropTarget, specificTarget: specificTarget, 
                data: specificTarget + "###" + targetX + "###" + targetY});
    }
});
