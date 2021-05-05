package org.jetbrains.jwm;

import lombok.*;

@Data
public class KeyboardEvent implements Event {
    public final int _keyCode;
    public final boolean _isPressed;
}