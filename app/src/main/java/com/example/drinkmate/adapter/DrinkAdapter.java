package com.example.drinkmate.adapter;

import android.content.Context;
// import android.content.Intent; // Wird hier nicht mehr direkt benötigt
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
// import com.example.drinkmate.DrinkDetailActivity; // Wird hier nicht mehr direkt benötigt
import com.example.drinkmate.R;
import com.example.drinkmate.model.Drink;
import java.util.ArrayList;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinks;
    private Context context;
    private final OnItemClickListener listener; // Listener-Member

    // Interface für den Klick-Listener
    public interface OnItemClickListener {
        void onItemClick(Drink drink);
    }

    // Angepasster Konstruktor
    public DrinkAdapter(Context context, List<Drink> initialDrinks, OnItemClickListener listener) {
        this.context = context;
        this.drinks = (initialDrinks != null) ? new ArrayList<>(initialDrinks) : new ArrayList<>();
        this.listener = listener;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = (drinks != null) ? drinks : new ArrayList<>();
        notifyDataSetChanged(); // Behält notifyDataSetChanged für Einfachheit
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drink_item, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink currentDrink = drinks.get(position);
        holder.textViewDrinkName.setText(currentDrink.getStrDrink());
        holder.textViewAlcoholicStatus.setText(currentDrink.getStrAlcoholic());

        Glide.with(context)
                .load(currentDrink.getStrDrinkThumb())
                // .placeholder(R.drawable.ic_placeholder_image) // Entfernt
                .error(R.drawable.ic_error_image)
                .into(holder.imageViewDrink);
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    class DrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDrink;
        TextView textViewDrinkName;
        TextView textViewAlcoholicStatus;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDrink = itemView.findViewById(R.id.imageViewDrink);
            textViewDrinkName = itemView.findViewById(R.id.textViewDrinkName);
            textViewAlcoholicStatus = itemView.findViewById(R.id.textViewAlcoholicStatus);

            // Klick-Logik verwendet jetzt den übergebenen Listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(drinks.get(position));
                }
            });
        }
    }
}
