# frozen_string_literal: true

module KjuiTools
  module Compose
    module Helpers
      class ImportManager
        def self.get_imports_map
          {
            lazy_column: "import androidx.compose.foundation.lazy.LazyColumn",
            lazy_row: "import androidx.compose.foundation.lazy.LazyRow",
            background: "import androidx.compose.foundation.background",
            border: "import androidx.compose.foundation.border",
            shape: ["import androidx.compose.foundation.shape.RoundedCornerShape",
                    "import androidx.compose.ui.draw.clip"],
            text_align: "import androidx.compose.ui.text.style.TextAlign",
            text_overflow: "import androidx.compose.ui.text.style.TextOverflow",
            text_style: "import androidx.compose.ui.text.TextStyle",
            visual_transformation: "import androidx.compose.ui.text.input.PasswordVisualTransformation",
            shadow: "import androidx.compose.ui.draw.shadow",
            arrangement: "import androidx.compose.foundation.layout.Arrangement",
            keyboard_type: ["import androidx.compose.foundation.text.KeyboardOptions",
                            "import androidx.compose.ui.text.input.KeyboardType"],
            ime_action: "import androidx.compose.ui.text.input.ImeAction",
            button_colors: "import androidx.compose.material3.ButtonDefaults",
            button_padding: "import androidx.compose.foundation.layout.PaddingValues",
            text_decoration: "import androidx.compose.ui.text.style.TextDecoration",
            shadow_style: ["import androidx.compose.ui.text.TextStyle",
                           "import androidx.compose.ui.graphics.Shadow",
                           "import androidx.compose.ui.geometry.Offset"],
            switch_colors: "import androidx.compose.material3.SwitchDefaults",
            slider_colors: "import androidx.compose.material3.SliderDefaults",
            checkbox_colors: "import androidx.compose.material3.CheckboxDefaults",
            dropdown_menu: ["import androidx.compose.material3.DropdownMenu",
                            "import androidx.compose.material3.DropdownMenuItem",
                            "import androidx.compose.material.icons.Icons",
                            "import androidx.compose.material.icons.filled.ArrowDropDown",
                            "import androidx.compose.foundation.clickable"],
            outlined_text_field: "import androidx.compose.material3.OutlinedTextField",
            icons: ["import androidx.compose.material.icons.Icons",
                    "import androidx.compose.material.icons.filled.*",
                    "import androidx.compose.material.icons.outlined.*"],
            icon_button: "import androidx.compose.material3.IconButton",
            clickable: "import androidx.compose.foundation.clickable",
            radio_colors: "import androidx.compose.material3.RadioButtonDefaults",
            tab_row: ["import androidx.compose.material3.TabRow",
                      "import androidx.compose.material3.Tab"],
            async_image: "import coil.compose.AsyncImage",
            content_scale: "import androidx.compose.ui.layout.ContentScale",
            lazy_grid: ["import androidx.compose.foundation.lazy.grid.LazyVerticalGrid",
                        "import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid",
                        "import androidx.compose.foundation.lazy.grid.GridCells"],
            webview: ["import android.webkit.WebView",
                      "import android.webkit.WebViewClient",
                      "import android.webkit.WebChromeClient",
                      "import androidx.compose.ui.viewinterop.AndroidView"],
            constraint_layout: ["import androidx.constraintlayout.compose.ConstraintLayout",
                                "import androidx.constraintlayout.compose.Dimension"],
            remember_state: ["import androidx.compose.runtime.remember",
                             "import androidx.compose.runtime.mutableStateOf",
                             "import androidx.compose.runtime.getValue",
                             "import androidx.compose.runtime.setValue"],
            remember: "import androidx.compose.runtime.remember",
            LaunchedEffect: "import androidx.compose.runtime.LaunchedEffect",
            bias_alignment: "import androidx.compose.ui.BiasAlignment",
            circle_shape: "import androidx.compose.foundation.shape.CircleShape",
            alpha: "import androidx.compose.ui.draw.alpha",
            image: "import androidx.compose.foundation.Image",
            painter_resource: "import androidx.compose.ui.res.painterResource",
            r_class: "import com.example.kotlinjsonui.sample.R",
            gradient: "import androidx.compose.ui.graphics.Brush",
            blur: "import androidx.compose.ui.draw.blur",
            navigation: ["import androidx.navigation.NavController",
                         "import androidx.navigation.compose.NavHost",
                         "import androidx.navigation.compose.composable",
                         "import androidx.navigation.compose.rememberNavController"],
            selectbox_component: "import com.kotlinjsonui.components.SelectBox",
            date_selectbox_component: "import com.kotlinjsonui.components.DateSelectBox",
            simple_date_selectbox_component: "import com.kotlinjsonui.components.SimpleDateSelectBox",
            visibility_wrapper: "import com.kotlinjsonui.components.VisibilityWrapper",
            custom_textfield: ["import com.kotlinjsonui.components.CustomTextField",
                               "import com.kotlinjsonui.components.CustomTextFieldWithMargins"],
            annotated_string: ["import androidx.compose.ui.text.AnnotatedString",
                               "import androidx.compose.ui.text.buildAnnotatedString",
                               "import androidx.compose.ui.text.SpanStyle",
                               "import androidx.compose.ui.text.withStyle"],
            clickable_text: "import androidx.compose.foundation.text.ClickableText",
            partial_attributes_text: ["import com.kotlinjsonui.components.PartialAttributesText",
                                      "import com.kotlinjsonui.components.PartialAttribute"],
            segment: "import com.kotlinjsonui.components.Segment",
            dynamic_mode_manager: "import com.kotlinjsonui.core.DynamicModeManager",
            safe_dynamic_view: "import com.kotlinjsonui.components.SafeDynamicView",
            circular_progress_indicator: "import androidx.compose.material3.CircularProgressIndicator",
            wrapContentSize: "import androidx.compose.foundation.layout.wrapContentSize",
            box: "import androidx.compose.foundation.layout.Box"
          }
        end
        
        def self.update_imports(content, required_imports)
          imports_map = get_imports_map
          
          required_imports.each do |import_key|
            import_lines = imports_map[import_key]
            next unless import_lines
            
            if import_lines.is_a?(Array)
              import_lines.each do |import_line|
                unless content.include?(import_line)
                  # Add import after the last import statement
                  if content =~ /^(import .+\n)+/m
                    last_import_end = $~.end(0)
                    content.insert(last_import_end, "#{import_line}\n")
                  end
                end
              end
            else
              unless content.include?(import_lines)
                # Add import after the last import statement
                if content =~ /^(import .+\n)+/m
                  last_import_end = $~.end(0)
                  content.insert(last_import_end, "#{import_lines}\n")
                end
              end
            end
          end
          
          content
        end
      end
    end
  end
end