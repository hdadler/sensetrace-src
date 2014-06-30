package com.ipv.sensetrace.commandservice;

class Timer extends Thread {
	
	private boolean killAfterOneHour=true;
	
	public void SetKillAfterOneHour(boolean flag)
	{
		killAfterOneHour=flag;
	}
	  
    public void run() {
      for(int i = 0; i < 61; i++) {
        try {
          sleep(60000);
        }
        catch(InterruptedException e) {
        }
       // System.out.println("Timer");
      
      //Exit programm if not defused
      if (i>=59 && killAfterOneHour )
      {
    	  System.exit(0);
      }
      }
    }
  
  }