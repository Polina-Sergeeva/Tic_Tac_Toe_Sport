package sergeeva.tic_tac_toe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import sergeeva.tic_tac_toe.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var currentLvl = 0
    private var currentRules = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        val data = getSettingsInfo()
        currentLvl = data.lvl
        currentRules = data.rules

        when(currentRules){
            1 -> binding.checkBoxVertical.isChecked = true
            2 -> binding.checkBoxVertical.isChecked = true
            3 -> {
                binding.checkBoxVertical.isChecked = true
                binding.checkBoxHorizontal.isChecked = true
            }
            4 -> binding.checkBoxDiagonal.isChecked = true
            5 -> {
                binding.checkBoxDiagonal.isChecked = true
                binding.checkBoxVertical.isChecked = true
            }
            6 -> {
                binding.checkBoxDiagonal.isChecked = true
                binding.checkBoxHorizontal.isChecked = true
            }
            7 -> {
                binding.checkBoxDiagonal.isChecked = true
                binding.checkBoxVertical.isChecked = true
                binding.checkBoxHorizontal.isChecked = true
            }
        }

        if(currentLvl == 0){
            binding.prevLvl.visibility = View.INVISIBLE
        }else if(currentLvl == 2){
            binding.nextLvl.visibility = View.INVISIBLE
        }

        binding.infoLevel.text = resources.getStringArray(R.array.game_lvl)[currentLvl]

        binding.toBack.setOnClickListener {
            onBackPressed()
        }

        binding.prevLvl.setOnClickListener{
            currentLvl--

            if(currentLvl == 0){
                binding.prevLvl.visibility = View.INVISIBLE
            }else if(currentLvl == 1){
                binding.nextLvl.visibility = View.VISIBLE
            }
            binding.infoLevel.text = resources.getStringArray(R.array.game_lvl)[currentLvl]
            updateLvl(currentLvl)
        }

        binding.nextLvl.setOnClickListener{
            currentLvl++

            if(currentLvl == 1){
                binding.prevLvl.visibility = View.VISIBLE
            }else if(currentLvl == 2){
                binding.nextLvl.visibility = View.INVISIBLE
            }
            binding.infoLevel.text = resources.getStringArray(R.array.game_lvl)[currentLvl]
            updateLvl(currentLvl)
        }

        binding.checkBoxVertical.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                currentRules++
            }else{
                currentRules--
            }
            updateRules(currentRules)
        }
        binding.checkBoxHorizontal.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                currentRules+=2
            }else{
                currentRules-=2
            }
            updateRules(currentRules)
        }
        binding.checkBoxDiagonal.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                currentRules+=4
            }else{
                currentRules-=4
            }
            updateRules(currentRules)
        }
        setContentView(binding.root)
    }

    private fun updateLvl(lvl: Int){
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()){
            putInt(PREF_LVL, lvl)
            apply()
        }
        setResult(RESULT_OK)
    }
    private fun updateRules(rules: Int){
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit()){
            putInt(PREF_RULES, rules)
            apply()
        }
        setResult(RESULT_OK)
    }

    private fun getSettingsInfo() : SettingsInfo {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)){
            val lvl = getInt(PREF_LVL, 0)
            val rules = getInt(PREF_RULES, 7)

            return SettingsInfo(lvl, rules)
        }
    }

    data class SettingsInfo(val lvl : Int, val rules: Int)

    companion object{
        const val PREF_LVL = "pref_lvl"
        const val PREF_RULES = "pref_rules"
    }
}