# La promoción de cada producto se lee por reflection desde la anotación @Promocionable.
# Si se activa la minificación (isMinifyEnabled = true), estas reglas conservan la anotación.
-keepattributes RuntimeVisibleAnnotations
-keep @interface com.example.pizzeriaapp.domain.Promocionable
-keep @com.example.pizzeriaapp.domain.Promocionable class *
