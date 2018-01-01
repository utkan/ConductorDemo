package com.bluelinelabs.conductor.demo.changehandler

import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandlerCompat

class ArcFadeMoveChangeHandlerCompat : TransitionChangeHandlerCompat(ArcFadeMoveChangeHandler(), FadeChangeHandler())
