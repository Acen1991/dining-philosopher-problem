package remote.client.philosophertest;

/* DistributedPhilosopher.java
*
* Part of the CPS221 Sockets Lab
*
*/

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/** An object that represents a single philosopher in a distributed version of 
*  the dining philosophers problem.  Each philosopher communicates with
*  neighbors via TCP.
*
* copyright (c) 2000, 2001, 2002, 2010, 2011 - Russell C. Bjork
*
*/
public class DistributedPhilosopher extends Observable
{
   /** Constructor
    *
    *  @param philosopherName the name to display for this philosopher
    *  @param leftNeighbor the left neighbor of this philosopher - null
    *         if not yet specified
    *  @param rightNeighbor the right neighbor of this philosopher - null
    *         if not yet specified
    *  @param pickupLeftFirst true if this philosopher picks up the left
    *         chopstick first
    */
   public DistributedPhilosopher(String philosopherName,
                      InetAddress leftNeighbor,
                      InetAddress rightNeighbor,
                      boolean pickupLeftFirst) throws IOException
   {
       this.philosopherName = philosopherName;
       neighbor[LEFT] = leftNeighbor;
       neighbor[RIGHT] = rightNeighbor;
       this.pickupLeftFirst = pickupLeftFirst;
       state = State.HUNGRY;

       // Connect to each neighbor if one is specified, or create a thread to
       // listen for a connection request from the neighbor if none specified.
       // When connecting to a neighbor, our left neighbor sees us as its right
       // neighbor and vice versa, which is reflected in the way the port is
       // specified in the connect request

       if (leftNeighbor != null)
       {
           communicateWithNeighborSocket[LEFT] = new Socket();
           communicateWithNeighborSocket[LEFT].connect(
                   new InetSocketAddress(leftNeighbor,
                                         COMMUNICATE_WITH_NEIGHBOR_PORT[RIGHT]));
       }
       else
           new Thread() {
               public void run()
               {
                   listenForConnectionRequest(LEFT);
               }
           }.start();

       if (rightNeighbor != null)
       {
           communicateWithNeighborSocket[RIGHT] = new Socket();
           communicateWithNeighborSocket[RIGHT].connect(
                   new InetSocketAddress(rightNeighbor,
                                         COMMUNICATE_WITH_NEIGHBOR_PORT[LEFT]));
       }
       else
           new Thread() {
               public void run()
               {
                   listenForConnectionRequest(RIGHT);
               }
           }.start();
   }

   /** Method executed by a thread that listens for incoming connection
    *  requests
    *
    *  @param side the side on which to listen - one of LEFT, RIGHT
    */
   private void listenForConnectionRequest(int side)
   {
       ServerSocket serverSocket = null;
       try
       {
           serverSocket = new ServerSocket(COMMUNICATE_WITH_NEIGHBOR_PORT[side]);
       }
       catch(IOException e)
       {
           System.err.println("Error creating server socket on " + 
                              SIDE_NAME[side] + " " + e);
           System.exit(1);
       }

       while (true)
       {
           Socket connectionSocket = null;
           try
           {
               connectionSocket = serverSocket.accept();
           }
           catch(IOException e)
           {
               System.err.println("Error accepting connection on " + 
				                   SIDE_NAME[side] + " " + e);
               System.exit(1);
           }

           synchronized(this)
           {
               setNeighbor(side, connectionSocket.getInetAddress());
               communicateWithNeighborSocket[side] = connectionSocket;
               notifyAll();
           }
       }
   }

   /** Set a neighbor when a connection is established
    *
    *  @param side the side the neighbor is on
    *  @param newNeighbor the neighbor
    */
   private void setNeighbor(int side, InetAddress newNeighbor)
   {
       neighbor[side] = newNeighbor;
       setChanged();
       if (side == LEFT)
           notifyObservers(Change.LEFT_NEIGHBOR_CHANGED);
       else
           notifyObservers(Change.RIGHT_NEIGHBOR_CHANGED);
   }

   /** Get the name of this philosopher
    *
    *  @return the name
    */
   public String getPhilosopherName()
   {
       return philosopherName;
   }

   /** Get the left neighbor of this philosopher
    *
    *  @return the left neighbor
    */
   public InetAddress getLeftNeighbor()
   {
       return neighbor[LEFT];
   }

   /** Get the right neighbor of this philosopher
    *
    *  @return the right neighbor
    */
   public InetAddress getRightNeighbor()
   {
       return neighbor[RIGHT];
   }

   /** Get the state of this philosopher
    *
    *  @return the state
    */
   public State getState()
   {
       return state;
   }

   /** Start this philosopher living once the display has been set up
    */
   public void startLiving()
   {
       // Create and start the threads that will listen messages from neighbors

       new Thread() {
           public void run()
           {
               listenForMessages(LEFT);
           }
       }.start();

       new Thread() {
           public void run()
           {
               listenForMessages(RIGHT);
           }
       }.start();

       // Create and start the thread that will actually simulate the philosopher
       new Thread() {
           public void run()
           {
               live();
           }
       }.start();
   }

   /** Simulate the philosophers' life - eating and thinking ...
    */
   private void live()
   {
       while (true)
       {
           switch(state)
           {
               case HUNGRY:
               
                   // Obtain the chopsticks.

                   if (pickupLeftFirst)
                   {
                       pickupChopstick(LEFT);
                       pickupChopstick(RIGHT);
                   }
                   else
                   {
                       pickupChopstick(RIGHT);
                       pickupChopstick(LEFT);
                   }
                    
                   // Can now eat
                   
                   state = State.EATING;
                   setChanged();
                   notifyObservers(Change.STATE_CHANGED);
                   break;
                           
               case EATING:

                   // Simulate eating by having the thread sleep for a 
                   // random time
                   
                   try
                   {
                       Thread.sleep((int) (AVERAGE_EAT_TIME *
                               (0.5 + Math.random())));
                   }
                   catch(InterruptedException e)
                   { }
                   
                   // Appetite now satisfied

                   state = State.SATED;
                   setChanged();
                   notifyObservers(Change.STATE_CHANGED);
                   break;
                   
               case SATED:
                                       
                   putdownChopstick(LEFT);
                   putdownChopstick(RIGHT);

                   // With hunger sated, can now think
                   
                   state = State.THINKING;
                   setChanged();
                   notifyObservers(Change.STATE_CHANGED);
                   break;
                   
               case THINKING:
               
                   // Simulate thinking by having the thread sleep for a
                   // random time
                   
                   try
                   {
                       Thread.sleep((int) (AVERAGE_THINK_TIME *
                               (0.5 + Math.random())));
                   }
                   catch(InterruptedException e)
                   { }
                   
                   // Hunger interrupts thinking - time to eat again
                   
                   state = State.HUNGRY;
                   setChanged();
                   notifyObservers(Change.STATE_CHANGED);
                   break;
           }
       }            
   }

   /** Pick up a chopstick
    *
    *  @param side the side
    */
   private synchronized void pickupChopstick(int side)
   {
       // If there is a neighbor on this side, we need to get permission to
       // use the chopstick.

       if (communicateWithNeighborSocket[side] != null)
       {
           // Make a request

           sendToNeighbor(side, REQUEST_MESSAGE);

           // Wait for the neighbor to give permission

           waitingForChopstick[side] = true;
           while (! hasChopstick[side])
               try
               {
                   wait();
               }
               catch(InterruptedException e)
               { }
           waitingForChopstick[side] = false;
       }
       else
           // Otherwise, we can just go ahead and use it.
           
           hasChopstick[side] = true;

       // Update display to show we have the chopstick

       setChanged();
       if (side == LEFT)
           notifyObservers(Change.LEFT_CHOPSTICK_PICKED_UP);
       else
           notifyObservers(Change.RIGHT_CHOPSTICK_PICKED_UP);
   }

   /** Put down a chopstick
    *
    *  @param side the side
    */
   private synchronized void putdownChopstick(int side)
   {
       // Put down the chopstick

       hasChopstick[side] = false;
       setChanged();

       // Update the display to show we don't have it anymore

       if (side == LEFT)
           notifyObservers(Change.LEFT_CHOPSTICK_PUT_DOWN);
       else
           notifyObservers(Change.RIGHT_CHOPSTICK_PUT_DOWN);

       // If the neighbor is waiting for it, give permission to use it

       if (neighborHasRequestedChopstick[side])
       {
           neighborHasRequestedChopstick[side] = false;
           sendToNeighbor(side, GRANT_MESSAGE);
       }
   }

   /** Send a message to a neighbor
    *
    *  @param side the side of send to
    *  @param message the message to send
    *  @exception IOException if an exception was thrown during communication
    */
   private void sendToNeighbor(int side, byte [] message)
   {
       try
       {
           communicateWithNeighborSocket[side].getOutputStream().write(message);
       }
       catch(IOException e)
       {
           System.err.println("Error sending " + (char) message[0] + " to " +
                              SIDE_NAME[side] + " " + e);
           System.exit(1);
       }


   }

   /** Method executed by a thread that listens for incoming messages
    *
    *  @param side the side on which to listen - one of LEFT, RIGHT
    */
   private void listenForMessages(int side)
   {
       while(true)
       {
           byte [] messageReceived = receiveMessage(side);
           switch(messageReceived[0])
           {
               case REQUEST_CODE:

                   if (hasChopstick[side])
                       neighborHasRequestedChopstick[side] = true;
                   else if (side == RIGHT && waitingForChopstick[side])
                       neighborHasRequestedChopstick[side] = true;
                   else
                       sendToNeighbor(side, GRANT_MESSAGE);
                   break;

               case GRANT_CODE:

                   synchronized(this)
                   {
                       waitingForChopstick[side] = false;
                       hasChopstick[side] = true;
                       notifyAll();
                   }
                   break;
           }
       }
   }

   /** Receive a message from a neighbor.  This method blocks until a
    *  message is received
    *
    *  @param side the side to receive from
    *  @return the message received from this neighbor
    */
   private byte [] receiveMessage(int side)
   {
       // Cannot start listening until we have a connection to neighbor

       while(communicateWithNeighborSocket[side] == null)
       {
           synchronized(this)
           {
               try
               {
                   wait();
               }
               catch(InterruptedException e)
               { }
           }
       }

       byte [] buffer = new byte[MESSAGE_LENGTH];
       try
       {
           communicateWithNeighborSocket[side].getInputStream().
                   read(buffer);
       }
       catch(IOException e)
       {
           System.err.println("Error reading message from " +
                              SIDE_NAME[side] + " " + e);
           System.exit(1);
       }
       return buffer;
   }

   /** The state of a philosopher
    */
   public enum State
   {
       HUNGRY,
       EATING,
       SATED,
       THINKING;
   }
   
   /** The nature of a change to this philosopher.  Used when notifying an
    *  observing display
    */
   public enum Change
   {
       STATE_CHANGED,
       LEFT_CHOPSTICK_PICKED_UP,
       RIGHT_CHOPSTICK_PICKED_UP,
       LEFT_CHOPSTICK_PUT_DOWN,
       RIGHT_CHOPSTICK_PUT_DOWN,
       LEFT_NEIGHBOR_CHANGED,
       RIGHT_NEIGHBOR_CHANGED;
   }

   // Constants that specify a direction

   private static final int LEFT = 0;
   private static final int RIGHT = 1;

   // Names for directions

   private static final String [] SIDE_NAME = { "left", "right" };

   // Instance variables - information passed to the constructor
   
   private String philosopherName;
   private boolean pickupLeftFirst;
  
   // Instance variable - the philosopher's current state

   private State state;
   
   // Information about neighbors and chopsticks.  In each case, the [0]
   // element of the array refers to the LEFT neighbor and the [1] element
   // to the RIGHT neighbor

   private int [] COMMUNICATE_WITH_NEIGHBOR_PORT = { 42420, 42421 };
   private InetAddress [] neighbor = new InetAddress[2];
   private Socket [] communicateWithNeighborSocket = new Socket[2];
   private boolean [] waitingForChopstick = new boolean[2];
   private boolean [] hasChopstick = new boolean[2];
   private boolean [] neighborHasRequestedChopstick = new boolean[2];
   
   /** Length of messages passed between neighbors
    */
   private static final int MESSAGE_LENGTH = 1;

   /** Messages sent to neighbor to request/grant chopsticks. 
    */
   private static final byte REQUEST_CODE = 'R';
   private static final byte GRANT_CODE = 'G';
   private static final byte [] REQUEST_MESSAGE = { REQUEST_CODE };
   private static final byte [] GRANT_MESSAGE = { GRANT_CODE };
   
   /** Constants that control the amount of time spent eating, thinking. The
    * actual time will be a random number between 0.5 and 1.5 times average.
    */
   private static final int AVERAGE_EAT_TIME = 5000;       // Milliseconds
   private static final int AVERAGE_THINK_TIME = 5000;     // Milliseconds
}