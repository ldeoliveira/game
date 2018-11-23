package com.bol.game.controllers;

import org.junit.Assert;
import org.junit.Test;

public class WebControllerTest {

    @Test
    public void testIndex() {
        Assert.assertTrue(new WebController().getIndex().equals("index"));
    }

}
