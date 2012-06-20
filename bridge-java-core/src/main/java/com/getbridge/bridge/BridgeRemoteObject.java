package com.getbridge.bridge;

/**
 * BridgeRemoteObject is an interface that defines the methods that a remote Bridge Object
 * is expected to conform to. 
 * 
 * Users should extend this interface and supply their list of
 * expected methods, with appropriate type signatures. This allows the Bridge library to create
 * proxy objects which forward calls to those methods.
 * 
 * The BridgeRemoteObject interface -- or an interface that extends it -- should be used as the parameter type
 * in RPC calls where a remote Bridge Object is expected as an argument.
 * 
 * @author sridatta
 */
public interface BridgeRemoteObject extends BridgeObjectBase {
}