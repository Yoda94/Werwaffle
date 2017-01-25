package com.example.philip.werwaffle.state;

/**
 * Created by Deathlymad on 25.01.2017.
 */

public enum VoteEnum {

    DummyVote("Dummy Vote"),
    VillageVote("Village Vote"),
    WerewolfVote("Werewolf Vote"),
    WhiteWerewolfVote("Werewolf Vote");

    private String name;

    private VoteEnum( String typeName)
    {
        this.name = typeName;
    }
}
