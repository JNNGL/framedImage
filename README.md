## FramedImage

High performance Bukkit/Folia plugin for map-arts.

<b>Features</b>:
<ul>
<li><b>Performance.</b> The plugin inserts thousands of frames in just a few seconds (during testing, the plugin inserted ~3000 frames in 2 seconds)</li>
<li><b>Dithering.</b> With dithering transitions look smoother and a small number of colors in the palette of maps becomes almost invisible.</li>
<li><b>Adaptive palette.</b> The player will always see images with the palette relevant to his version, regardless of the server version.</li>
<li><b>Client-side.</b> The frames are sent to the players directly and are never created on the server itself.</li>
<li><b>GIF support.</b></li>
<li><b>Glowing item frame support.</b> Works on 1.17+ clients, regardless of the server version.</li>
</ul>

<b>Commands:</b>
<ul>
<li><i>/fi create <width> <height> <imageUrl></i><p>Inserts an image from the link on the block where the cursor points. (Frames go right and up)</p></li>
<li><i>/fi remove</i><p>Deletes the image that the cursor points to.</p></li>
<li><i>/fi reload</i><p>Reloads the config and all images</p></li>
</ul>

<b>Permissions:</b>
<ul>
<li><i>framedimage.command</i><p>Access to the plugin command</p></li>
<li><i>framedimage.subcommand.create</i><p>Ability to create images</p></li>
<li><i>framedimage.subcommand.remove</i><p>Ability to delete images</p></li>
<li><i>framedimage.subcommand.reload</i><p>Ability to reload the plugin</p></li>
</ul>
