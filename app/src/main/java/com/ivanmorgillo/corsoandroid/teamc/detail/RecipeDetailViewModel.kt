package com.ivanmorgillo.corsoandroid.teamc.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamc.exhaustive

class RecipeDetailViewModel : ViewModel() {
    val states = MutableLiveData<RecipeDetailScreenStates>()

    fun send(event: RecipeDetailScreenEvent) {
        when (event) {
            RecipeDetailScreenEvent.OnScreenRecipeDetailReady -> {
                loadRecipeDetailContent()
            }
        }.exhaustive
    }

    private fun loadRecipeDetailContent() {
        states.postValue(RecipeDetailScreenStates.Loading)
        val recipeInstructions = "To make the pastry, measure the flour into " +
                "a bowl and rub in the butter with your fingertips until the " +
                "mixture resembles fine breadcrumbs. Add the water, mixing to " +
                "form a soft dough. Roll out the dough on a lightly floured work " +
                "surface and use to line a 20cm/8in flan tin. Leave in the fridge to " +
                "chill for 30 minutes. Preheat the oven to 200C/400F/Gas 6 (180C fan). " +
                "Line the pastry case with foil and fill with baking beans. Bake blind for " +
                "about 15 minutes, then remove the beans and foil and cook for a further five minutes " +
                "to dry out the base. For the filing, spread the base of the flan generously with " +
                "raspberry jam. Melt the butter in a pan, take off the heat and then stir in the sugar. " +
                "Add ground almonds, egg and almond extract. Pour into the flan tin and sprinkle over the " +
                "flaked almonds. Bake for about 35 minutes. If the almonds seem to be browning too quickly," +
                " cover the tart loosely with foil to prevent them burning."
        val strUri = "https://discord.com/channels/798489874746703873/803643080187052072/809752533949612063"
        val recipeDetail = RecipeDetail(
            recipeName = "Spicy Arrabiata Penne",
            recipeCategory = "Vegetarian",
            recipeArea = "Italian",
            recipeInstructions = recipeInstructions,
            recipeImage = "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg",
            recipeIngredientsAndMeasures = mapOf(RecipeIngredients("pasta") to RecipeMeasures("1/2 cup")),
            recipeVideoInstructions = strUri
        )
        states.postValue(RecipeDetailScreenStates.Content(recipeDetail))
    }
}

sealed class RecipeDetailScreenEvent {
    object OnScreenRecipeDetailReady : RecipeDetailScreenEvent()
}

sealed class RecipeDetailScreenStates {
    object Loading : RecipeDetailScreenStates()
    object Error : RecipeDetailScreenStates()
    data class Content(val recipeDetail: RecipeDetail) : RecipeDetailScreenStates()
}
