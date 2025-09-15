package com.example.drinkmate.ui.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drinkmate.R;
import com.example.drinkmate.data.local.FavoriteDrink;
import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<FavoriteDrink> favoriteDrinks = new ArrayList<>();
    private OnUnfavoriteClickListener unfavoriteClickListener;
    private OnFavoriteItemClickListener favoriteItemClickListener; // Neu: Listener für Item-Klick
    private Context context;

    // Interface für den Klick auf das "Entfavorisieren"-Icon
    public interface OnUnfavoriteClickListener {
        void onUnfavoriteClick(FavoriteDrink favoriteDrink);
    }

    // Neu: Interface für den Klick auf das gesamte Item
    public interface OnFavoriteItemClickListener {
        void onFavoriteItemClick(FavoriteDrink favoriteDrink);
    }

    // Konstruktor erweitert, um beide Listener zu akzeptieren
    public FavoritesAdapter(Context context, OnUnfavoriteClickListener unfavoriteListener, OnFavoriteItemClickListener itemClickListener) {
        this.context = context;
        this.unfavoriteClickListener = unfavoriteListener;
        this.favoriteItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_drink_item, parent, false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteDrink currentFavorite = favoriteDrinks.get(position);
        holder.textViewName.setText(currentFavorite.getStrDrink());

        Glide.with(context)
                .load(currentFavorite.getStrDrinkThumb())
                .error(R.drawable.ic_error_image)
                .into(holder.imageViewDrink);

        // Klick-Listener für das "Entfavorisieren"-Icon bleibt
        holder.imageViewUnfavorite.setOnClickListener(v -> {
            if (unfavoriteClickListener != null) {
                unfavoriteClickListener.onUnfavoriteClick(currentFavorite);
            }
        });

        // Neu: Klick-Listener für das gesamte ItemView im ViewHolder
        // wird im ViewHolder-Konstruktor gesetzt
    }

    @Override
    public int getItemCount() {
        return favoriteDrinks.size();
    }

    public void setFavorites(List<FavoriteDrink> favorites) {
        this.favoriteDrinks = favorites != null ? favorites : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Innere ViewHolder-Klasse
    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewDrink;
        private TextView textViewName;
        private ImageView imageViewUnfavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDrink = itemView.findViewById(R.id.imageViewFavoriteDrink);
            textViewName = itemView.findViewById(R.id.textViewFavoriteDrinkName);
            imageViewUnfavorite = itemView.findViewById(R.id.imageViewUnfavorite);

            // Klick-Listener für das gesamte Item hier setzen
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && favoriteItemClickListener != null) {
                    favoriteItemClickListener.onFavoriteItemClick(favoriteDrinks.get(position));
                }
            });
        }
    }
}
