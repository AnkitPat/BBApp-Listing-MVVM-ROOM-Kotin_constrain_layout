package com.example.bbim1041.bbstore.view

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by BBIM1041 on 23/02/18.
 * Base Fragment for MVVM implementation
 */

open class MvvmFragment: Fragment() {
 val subscriptions = CompositeDisposable()
    
    fun subscribe(disposable: Disposable): Disposable {
        subscriptions.add(disposable)
        return disposable
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }
}
