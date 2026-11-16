package com.kotlinjsonui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The keyboard action a CustomTextField's field performs (jsonui-cli ticket
 * kjui-textfield-keyboard-actions-never-reach-the-field). The device arm is
 * library-dynamic's DynamicDeclaredEventDeliveryTest; this pins the two
 * spellings of "no returnKeyType": Dynamic's options say Default, kjui
 * codegen passes KeyboardOptions.Default, which says Unspecified — the
 * runtime census measured the codegen field's Enter at 0 calls while
 * Dynamic's was 1, until both were read as Done.
 */
class CustomTextFieldKeyboardActionTest {
    private val done = KeyboardActions(onDone = {})

    @Test
    fun aSingleLineFieldWithNoDeclaredActionGetsDone() {
        assertEquals(ImeAction.Done, submitOptions(KeyboardOptions.Default, done, singleLine = true).imeAction)
        assertEquals(ImeAction.Done, submitOptions(KeyboardOptions(imeAction = ImeAction.Default), done, singleLine = true).imeAction)
    }

    @Test
    fun aDeclaredActionAMultiLineFieldOrNoDoneActionIsKept() {
        assertEquals(ImeAction.Search, submitOptions(KeyboardOptions(imeAction = ImeAction.Search), done, singleLine = true).imeAction)
        assertEquals(KeyboardOptions.Default.imeAction, submitOptions(KeyboardOptions.Default, done, singleLine = false).imeAction)
        assertEquals(KeyboardOptions.Default.imeAction, submitOptions(KeyboardOptions.Default, KeyboardActions(), singleLine = true).imeAction)
    }

    @Test
    fun theHandlerRunsTheActionTheImeActionNames() {
        val calls = mutableListOf<String>()
        val actions = KeyboardActions(onDone = { calls += "done" }, onSearch = { calls += "search" })
        keyboardActionHandler(actions, KeyboardOptions.Default.imeAction, singleLine = true)!!.onKeyboardAction { calls += "default" }
        keyboardActionHandler(actions, ImeAction.Search, singleLine = true)!!.onKeyboardAction { calls += "default" }
        assertEquals(listOf("done", "search"), calls)
        assertNull(keyboardActionHandler(actions, ImeAction.Go, singleLine = true))
    }
}
