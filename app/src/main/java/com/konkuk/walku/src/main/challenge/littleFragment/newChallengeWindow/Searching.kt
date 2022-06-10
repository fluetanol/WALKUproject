package com.konkuk.walku.src.main.challenge.littleFragment.newChallengeWindow

import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import java.util.*

class Searching() {
    var initialarray = arrayOf ('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ',
        'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' )
    var middlearray = arrayOf('ㅏ' ,'ㅐ' ,'ㅑ', 'ㅒ', 'ㅓ' ,'ㅔ' ,'ㅕ' ,'ㅖ' ,
        'ㅗ', 'ㅘ' ,'ㅙ' ,'ㅚ' ,'ㅛ', 'ㅜ' ,'ㅝ', 'ㅞ' ,'ㅟ' ,'ㅠ' ,'ㅡ' ,'ㅢ', 'ㅣ')
    var lastarray =arrayOf('ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ',
        'ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ')
    var frontarray = mutableListOf<ChallengeNewData>()
    var stack = Stack<ArrayList<ChallengeNewData>>()

    fun initarray(data:ArrayList<ChallengeNewData>){
        stack.add(data)
    }

    fun SearchFunction(inputstr:String, flag:Int,data:ArrayList<ChallengeNewData>){
        var pass =true
        stack.add(data)
        if(stack.size>1 && flag== ChallengeNewChallengeFragment.SEARCHFUNC_FLAG_INPUTSIZE_SMALLER)
            stack.pop()

        for (i in stack.peek()){
            if(inputstr.isEmpty())
                break
            if(i.context.length<inputstr.length) continue
            else {
                val initial=findinitialword(i.context[inputstr.length - 1])
                val middle = findmiddleword(i.context[inputstr.length - 1])
                val final= findfinalword(i.context[inputstr.length - 1])
                val inputinitial = findinitialword(inputstr[inputstr.length - 1])
                val inputmiddle= findmiddleword(inputstr[inputstr.length - 1])
                val inputfinal= findfinalword(inputstr[inputstr.length - 1])

                if (initial == inputinitial && inputmiddle=='x') {
                    pass=false
                    frontarray.add(i)
                }//inputstr가 초성만 들어오면 이부분을 참고하게 됨
                else if (initial == inputinitial && middle==inputmiddle && inputfinal =='x') {
                    pass=false
                    frontarray.add(i)
                }//inputstr가 초성+중성 조합이면 이 부분을 참고하게 됨

                else if (i.context[inputstr.length - 1] == inputstr[inputstr.length - 1]) {
                    pass=false
                    frontarray.add(i)
                }//초성+중성+종성 모두 다 같은경우
            }

        }

        if(flag== ChallengeNewChallengeFragment.SEARCHFUNC_FLAG_INPUTSIZE_BIGGER) {
            var temparray = ArrayList<ChallengeNewData>()
            for (i in frontarray) temparray.add(i)
            stack.add(temparray)
        }
        for(i in 0..frontarray.size-1) {
            try {
                Log.i("!!", frontarray[i].context)
            }catch(e:Exception) {
            Log.e("!!","$e")
            }
        }
        frontarray.clear()
    }


    private fun findinitialword(word:Char):Char{
        if (word.code - 'ㄱ'.code in 1..29)
            return word
        val position =(word.code- ChallengeNewChallengeFragment.HANGUEL_FIRST_UNICODE)/ ChallengeNewChallengeFragment.HANGUEL_DIFFERENCE_UNICODE
        if(position>=0) return initialarray[position]
        else return 'x'
    }

    private fun findmiddleword(word:Char):Char{
        val exceptinitial = (word.code - ChallengeNewChallengeFragment.HANGUEL_FIRST_UNICODE)% ChallengeNewChallengeFragment.HANGUEL_DIFFERENCE_UNICODE //초성을 제외한 값
        val position = exceptinitial/ ChallengeNewChallengeFragment.HANGUEL_MIDDLECOUNT_UNICODE
        if(position>=0) return middlearray[position]
        else return 'x'
    }

    private fun findfinalword(word:Char):Char{
        val position = ((word.code - ChallengeNewChallengeFragment.HANGUEL_FIRST_UNICODE)% ChallengeNewChallengeFragment.HANGUEL_MIDDLECOUNT_UNICODE)-1
        if(position>=0) return lastarray[position]
        else return 'x'
    }

}