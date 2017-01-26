package com.example.philip.werwaffle.state;

import java.util.Map;

/**
 * Created by Deathlymad on 25.01.2017.
 */

public class Vote {
    private VoteEnum voteType;

    private Map<String, Integer> voteMap;
    private String localChoice;

    public Vote( VoteEnum voteType)
    {
        this.voteType = voteType;
        //voteMap = NetContext.getClientNameList()
        //only generate List for applicable Targets
        localChoice = "";
    }

    public int getVoteOn(String name)
    {
        return voteMap.get(name);
    }

    private void setVoteOn(String name, int value)
    {
        if ( voteMap.containsKey(name))
            voteMap.remove(name);
        voteMap.put(name, value);
        //Net Update
    }

    public void onUpdate(String data)
    {
        int pos = data.indexOf(':');
        String name = data.substring(0, pos);
        int amount = Integer.getInteger(data.substring(pos + 1));
        if ( voteMap.containsKey(name))
            voteMap.remove(name);
        voteMap.put(name, amount);
    }

    public void select( String name)
    {
        if (!localChoice.isEmpty())
        {
            setVoteOn( localChoice, getVoteOn(localChoice) - 1);
        }

        setVoteOn( name, getVoteOn(name) + 1);
        localChoice = name;
    }

    public VoteEnum getVoteType()
    {
        return voteType;
    }

    public String[] getVoteeList()
    {
        return (String[])voteMap.keySet().toArray();
    }
}
