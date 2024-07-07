package me.truewin.StoneArtifacts;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
	
	private Random random = new Random();
	
	private int maxIntValue = 100000;
	
	private Comparator<Integer> integerComparator = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			if(o1 > o2) return 1;
			else if(o1 < o2) return -1;
			else return 0;
		}
	};
	
	private ItemStack artifact = new ItemStack(Material.FLOWER_POT, 1);
	private ItemMeta artifactMeta = artifact.getItemMeta();
	private Range<Integer> artifactDropProbability = new Range<Integer>(0, 400);
	
	private ItemStack artifactBlock = new ItemStack(Material.BRICKS, 1);
	private ItemMeta artifaceBlockMeta = artifactBlock.getItemMeta();
	
	private ItemStack villagerSpawner = new ItemStack(Material.MELON, 1);
	private ItemMeta villagerMeta = villagerSpawner.getItemMeta();
	
	
	private HashMap<Range<Integer>, ItemStack[]> artifactDropsProbabilityMap = new HashMap<Range<Integer>, ItemStack[]>();
	private HashMap<Location, ItemStack> artifactBlockMap = new HashMap<Location, ItemStack>();
	
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("Enabling StoneArtifacts 1.0");
		getServer().getPluginManager().registerEvents(this, this);
		
		artifactMeta.addEnchant(Enchantment.DURABILITY, 10, true);
		artifaceBlockMeta.addEnchant(Enchantment.DURABILITY, 10, true);
		villagerMeta.addEnchant(Enchantment.DIG_SPEED, 10, true);
		
		artifactMeta.setDisplayName("§6Artifact");
		artifaceBlockMeta.setDisplayName("§4Artifact Block");
		villagerMeta.setDisplayName("§2Villager Spawner");
		
		artifact.setItemMeta(artifactMeta);
		artifactBlock.setItemMeta(artifaceBlockMeta);
		villagerSpawner.setItemMeta(villagerMeta);
		
		artifactDropsProbabilityMap.put(new Range<Integer>(0, 1000), new ItemStack[] {
			new ItemStack(Material.IRON_GOLEM_SPAWN_EGG, 1),
			new ItemStack(Material.DROWNED_SPAWN_EGG, 1),
			new ItemStack(Material.WITCH_SPAWN_EGG, 1)
		});
		artifactDropsProbabilityMap.put(new Range<Integer>(1001, 2000), new ItemStack[] {
			new ItemStack(Material.SPAWNER, 1),
		});
		artifactDropsProbabilityMap.put(new Range<Integer>(2001, 12000), new ItemStack[] {
			new ItemStack(Material.CREEPER_SPAWN_EGG, 1),
			new ItemStack(Material.SKELETON_SPAWN_EGG, 1),
			new ItemStack(Material.ZOMBIE_SPAWN_EGG, 1),
			new ItemStack(Material.SLIME_SPAWN_EGG, 1),
			new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG, 1),
			new ItemStack(Material.SPIDER_SPAWN_EGG, 1),
			new ItemStack(Material.NETHERITE_INGOT, 4)
		});
		artifactDropsProbabilityMap.put(new Range<Integer>(12001, maxIntValue * 2), new ItemStack[] {
			new ItemStack(Material.EXPERIENCE_BOTTLE, 48),
			new ItemStack(Material.EMERALD, 24),
			new ItemStack(Material.DIAMOND, 16),
			new ItemStack(Material.GOLDEN_APPLE, 4),
			villagerSpawner
		});
		
		NamespacedKey artifactBlockKey = new NamespacedKey(this, "artifact_block_key");
		ShapedRecipe artifactBlockRecipe = new ShapedRecipe(artifactBlockKey, artifactBlock);
		
		ExactChoice artifactChoice = new ExactChoice(artifact);
		artifactBlockRecipe.shape("XXX", "XXX", "XXX");
		artifactBlockRecipe.setIngredient('X', artifactChoice);
		
		Bukkit.addRecipe(artifactBlockRecipe);
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("Disabling StoneArtifacts 1.0");
		artifactDropsProbabilityMap.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 3) return true;
		switch (cmd.getName()) {
			case "sagive": {
				if(!(sender instanceof Player)) return true;
				Player player = Bukkit.getPlayer(args[0]);
				switch (args[2]) {
					case "artifact": {
						ItemStack changedItemStack = artifact.clone();
						changedItemStack.setAmount(Integer.parseInt(args[1]));
						player.getWorld().dropItem(player.getLocation(), changedItemStack);
						return true;
					}
					case "artifact_block": {
						ItemStack changedItemStack = artifactBlock.clone();
						changedItemStack.setAmount(Integer.parseInt(args[1]));
						player.getWorld().dropItem(player.getLocation(), changedItemStack);
						return true;
					}
					default: break;
				}
				
			}
			default: break;
		}
		return false;
	}
	
	public ItemStack getItemDrop() {
		Integer randomInteger = random.nextInt(0, maxIntValue + 1);
		boolean state = false;
		for(Range<Integer> integerRange : artifactDropsProbabilityMap.keySet()) {
			state = integerRange.inRange(randomInteger, integerComparator);
			if(state) {
				ItemStack[] itemStacks = artifactDropsProbabilityMap.get(integerRange);
				return itemStacks[random.nextInt(0, itemStacks.length)];
			}
		}
		return new ItemStack(Material.STONE); 
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		switch(block.getType()) {
			case ANDESITE:
			case GRANITE:
			case DIORITE:
			case DEEPSLATE:
			case BLACKSTONE:
			case STONE: {
				Integer randomInteger = random.nextInt(0, maxIntValue + 1);
				if(artifactDropProbability.inRange(randomInteger, integerComparator)) {
					event.setDropItems(false);
					event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), artifact);
					event.setExpToDrop(69);
				}
				break;
			}
			case BRICKS: {
				Location blockLocation = block.getLocation();
				if(artifactBlockMap.containsKey(blockLocation)) {
					ItemStack itemStackAtLocation = artifactBlockMap.get(blockLocation);
					if(itemStackAtLocation.equals(artifactBlock)) {
						event.setDropItems(false);
						event.getPlayer().getWorld().dropItemNaturally(blockLocation, getItemDrop());
						artifactBlockMap.remove(blockLocation);
					}
				}
				break;
			}
			
			default: break;
		}
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack itemStackPlaced = event.getItemInHand().clone();
		itemStackPlaced.setAmount(1);
		if(itemStackPlaced.equals(artifactBlock)) {
			artifactBlockMap.put(event.getBlockPlaced().getLocation(), artifactBlock);
		} else if(itemStackPlaced.equals(villagerSpawner)) {
			event.getPlayer().getWorld().spawnEntity(event.getBlockPlaced().getLocation(), EntityType.VILLAGER);
			event.getBlockPlaced().setType(Material.AIR);
		} else if(itemStackPlaced.equals(artifact)) {
			event.setCancelled(true);
		}
	}
}













