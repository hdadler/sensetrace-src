package com.jtalis.core.terms;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.jtalis.core.event.EtalisEvent;
import com.jtalis.core.event.EventTimestamp;
import com.jtalis.core.plengine.logic.CompoundTerm;
import com.jtalis.core.plengine.logic.Term;

/**
 * AssertTEventTermerm
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev<a>
 */
public class EventTerm extends CompoundTerm implements Serializable {

	private static final long serialVersionUID = -4711210975087839013L;

	/*public EventTerm(){
		super();
	}

	public EventTerm(EtalisEvent event) {
		super("event", event);
	}

}*/
// changed by Pouyan
String timeStartsToString,timeEndsToString;

public EventTerm(EtalisEvent event) {
    super("event", event);//super("event",event);
    DateFormat df = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
    this.timeStartsToString = "datime("+ df.format(event.getTimeStarts()) +","+((EventTimestamp) event.getTimeStarts()).getIndex()+")";
    this.timeEndsToString = "datime("+df.format(event.getTimeEnds())+","+((EventTimestamp) event.getTimeEnds()).getIndex()+")";
}


//did not exist before, added by Pouyan
@Override
public String getPrologString() {
    if (getArity() == 0) {
        return this.getName();
    }
    StringBuilder builder = new StringBuilder(this.getName()).append("(");
    for (Term t : this.getTerms()) {
        builder.append(t.getPrologString()).append(", ");
    }
   
    if(this.timeStartsToString == null){
        builder.replace(builder.length() - 2, builder.length(), ")");
        return builder.toString();
    }
    else if(this.timeEndsToString == null){
        builder.append("[");
        builder.append(this.timeStartsToString);
        builder.append(",");
        builder.append(this.timeStartsToString);
        builder.append("]");

        builder.append(")");
        return builder.toString();
    }
    else{
        builder.append("[");

        builder.append(this.timeStartsToString);
        builder.append(", ");
        builder.append(this.timeEndsToString);
        builder.append("]");

        builder.append(")");
        //System.out.println("*************  "+builder.toString());
        return builder.toString();           
    }
           
}
//did not exist before
@Override
public String toString() {
    return getPrologString();
}

//Pouyan
}
