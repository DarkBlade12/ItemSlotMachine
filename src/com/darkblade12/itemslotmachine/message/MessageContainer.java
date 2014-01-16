package com.darkblade12.itemslotmachine.message;

public abstract interface MessageContainer {

	public abstract String plugin_prefix();

	public abstract String plugin_reloaded(String version, long time);

	public abstract String command_no_console_executor();

	public abstract String command_no_permission();

	public abstract String command_invalid_usage(String usage);

	public abstract String help_page_header(String label);

	public abstract String help_page_footer(int currentPage, int pageAmount);

	public abstract String help_page_command_format(String command, String description, boolean executableAsConsole, String permission);

	public abstract String help_page_not_existent(int page);

	public abstract String player_not_enough_space();

	public abstract String player_not_enough_space_other();

	public abstract String player_not_existent();

	public abstract String player_no_item_in_hand();

	public abstract String design_wand_name();

	public abstract String[] design_wand_lore();

	public abstract String design_wand_got();

	public abstract String design_wand_first_position_selected(int x, int y, int z, String world);

	public abstract String design_wand_second_position_selected(int x, int y, int z, String world);

	public abstract String design_already_existent();

	public abstract String design_not_existent();

	public abstract String design_invalid_selection();

	public abstract String design_creation_failure(String cause);

	public abstract String design_creation_success(String name);

	public abstract String design_removal(String name);

	public abstract String design_list(String list);

	public abstract String design_not_modifiable();

	public abstract String design_inversion(String name);

	public abstract String design_reload();

	public abstract String coin_name();

	public abstract String[] coin_lore();

	public abstract String coin_purchase_disabled();

	public abstract String coin_purchase_not_enough_money(int coins, double price, String currencyName);

	public abstract String coin_purchase(int coins, double price, String currencyName);

	public abstract String coin_grant_self(int coins);

	public abstract String coin_grant_sender(String player, int coins);

	public abstract String coin_grant_receiver(int coins, String sender);

	public abstract String slot_machine_modifying_not_allowed();

	public abstract String slot_machine_clicked(String name);

	public abstract String slot_machine_usage_not_allowed();

	public abstract String slot_machine_broken();

	public abstract String slot_machine_still_active();

	public abstract String slot_machine_creative_not_allowed();

	public abstract String slot_machine_not_enough_coins(int coins);

	public abstract String slot_machine_limited_usage(int amount);

	public abstract String slot_machine_locked(String player, int seconds);

	public abstract String slot_machine_won(double money, String currencyName, int itemAmount, String items);

	public abstract String slot_machine_lost();

	public abstract String slot_machine_already_existent();

	public abstract String slot_machine_not_existent();

	public abstract String slot_machine_building_failure(String cause);

	public abstract String slot_machine_building_success(String name);

	public abstract String slot_machine_destruction(String name);

	public abstract String slot_machine_list_empty();

	public abstract String slot_machine_list(String list);

	public abstract String slot_machine_teleportation_failure(String name, String cause);

	public abstract String slot_machine_teleportation_success(String name);

	public abstract String slot_machine_rebuilding(String name);

	public abstract String slot_machine_moving_failure(String cause);

	public abstract String slot_machine_moving_success(String name);

	public abstract String slot_machine_not_active();

	public abstract String slot_machine_deactivation(String name);

	public abstract String slot_machine_money_pot_not_enabled();

	public abstract String slot_machine_money_pot_empty();

	public abstract String slot_machine_money_pot_deposit(double money, String currencyName, String name, double pot);

	public abstract String slot_machine_money_pot_withdraw(double money, String currencyName, String name, double pot);

	public abstract String slot_machine_money_pot_set(String name, double money, String currencyName);

	public abstract String slot_machine_money_pot_reset(String name, double pot, String currencyName);

	public abstract String slot_machine_money_pot_clear(String name);

	public abstract String slot_machine_item_pot_not_enabled();

	public abstract String slot_machine_item_pot_empty();

	public abstract String slot_machine_item_pot_invalid_item_list(String cause);

	public abstract String slot_machine_item_pot_deposit(String item, String name);

	public abstract String slot_machine_item_pot_deposit_multiple(String items, String name);

	public abstract String slot_machine_item_pot_set(String name, String items);

	public abstract String slot_machine_item_pot_reset(String name, String items);

	public abstract String slot_machine_item_pot_clear(String name);

	public abstract String slot_machine_reload_failure(String cause);

	public abstract String slot_machine_reload(String name);

	public abstract String statistic_show_slot_machine(String name, String statistic);

	public abstract String statistic_player_not_existent();

	public abstract String statistic_show_player(String name, String statistic);

	public abstract String statistic_top_category_not_existent();

	public abstract String statistic_top_slot_machine_invalid_category();

	public abstract String statistic_top_slot_machine_not_existent();

	public abstract String statistic_top_slot_machine(String category, String top);

	public abstract String statistic_top_player_not_existent();

	public abstract String statistic_top_player(String category, String top);

	public abstract String statistic_reset_slot_machine(String name);

	public abstract String statistic_reset_player(String name);

	public abstract String sign_coin_shop_header();

	public abstract String sign_coin_shop_coins(int coins);

	public abstract String sign_coin_shop_price(double price);

	public abstract String sign_coin_shop_spacer();

	public abstract String sign_pot_money(double money);

	public abstract String sign_pot_items(int items);

	public abstract String sign_pot_spacer(String colorCode);

	public abstract String input_not_numeric(String input);

	public abstract String lower_than_number(int number);

	public abstract String equals_number(int number);

	public abstract String higher_than_number(double number);

	public abstract String invalid_amount(String cause);

	public abstract String and();

	public abstract String total_spins();

	public abstract String won_spins();

	public abstract String lost_spins();

	public abstract String coins_spent();

	public abstract String won_money();

	public abstract String won_items();
}