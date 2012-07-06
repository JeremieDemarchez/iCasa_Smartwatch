/**
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
package fr.orange.x10;


/** OperationTimedOutException is an exception to be thrown when an operation times out.
  * 
  * 
  * @author Wade Wassenberg
  * 
  * @version 1.0
  */

public class OperationTimedOutException extends Exception
{


    /** OperationTimedOutException constructs an OperationTimedOutException with no message
      * 
      * 
      */

    public OperationTimedOutException()
    {
        super();
    }


    /** OperationTimedOutException constructs an OperationTimedOutException with the specified message
      * 
      * @param message the error message associated to the exception.
      * 
      */
  
    public OperationTimedOutException(String message)
    { 
        super(message);
    }
}
