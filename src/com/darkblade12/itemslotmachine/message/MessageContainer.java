package com.darkblade12.itemslotmachine.message;

interface MessageContainer {

    String plugin_prefix();

    String plugin_reloaded(String version, long time);

    String command_no_console_executor();

    String command_no_permission();

    String command_invalid_usage(String usage);

    String help_page_header(String label);

    String help_page_footer(int currentPage, int pageAmount);

    String help_page_command_format(String command, String description, boolean executableAsConsole, String permission);

    String help_page_not_existent(int page);

    String player_not_enough_space();

    String player_not_enough_space_other();

    String player_not_existent();

    String player_no_item_in_hand();

    String design_wand_name();

    String[] design_wand_lore();

    String design_wand_got();

    String design_wand_first_position_selected(int x, int y, int z, String world);

    String design_wand_second_position_selected(int x, int y, int z, String world);

    String design_already_existent();

    String design_not_existent();

    String design_invalid_selection();

    String design_creation_failure(String cause);

    String design_creation_success(String name);

    String design_removal_failure(String name);

    String design_removal_success(String name);

    String design_list(String list);

    String design_not_modifiable();

    String design_inversion_failure(String name, String cause);
    
    String design_inversion_success(String name);

    String design_reload();

    String coin_name();

    String[] coin_lore();

    String coin_purchase_disabled();

    String coin_purchase_not_enough_money(int coins, double price, String currencyName);

    String coin_purchase(int coins, double price, String currencyName);

    String coin_grant_self(int coins);

    String coin_grant_sender(String player, int coins);

    String coin_grant_receiver(int coins, String sender);

    String slot_machine_modifying_not_allowed();

    String slot_machine_clicked(String name);

    String slot_machine_usage_not_allowed();

    String slot_machine_broken();

    String slot_machine_still_active();

    String slot_machine_creative_not_allowed();

    String slot_machine_not_enough_coins(int coins);

    String slot_machine_limited_usage(int amount);

    String slot_machine_locked(String player, int seconds);

    String slot_machine_won(double money, String currencyName, int itemAmount, String items);

    String slot_machine_lost();

    String slot_machine_already_existent();

    String slot_machine_not_existent();

    String slot_machine_building_failure(String cause);

    String slot_machine_building_success(String name);

    String slot_machine_destruction(String name);

    String slot_machine_list_empty();

    String slot_machine_list(String list);

    String slot_machine_teleportation_failure(String name, String cause);

    String slot_machine_teleportation_success(String name);

    String slot_machine_rebuilding_failure(String name, String cause);
    
    String slot_machine_rebuilding_success(String name);

    String slot_machine_moving_failure(String cause);

    String slot_machine_moving_success(String name);

    String slot_machine_not_active();

    String slot_machine_deactivation(String name);

    String slot_machine_money_pot_not_enabled();

    String slot_machine_money_pot_empty();

    String slot_machine_money_pot_deposit(double money, String currencyName, String name, double pot);

    String slot_machine_money_pot_withdraw(double money, String currencyName, String name, double pot);

    String slot_machine_money_pot_set(String name, double money, String currencyName);

    String slot_machine_money_pot_reset(String name, double pot, String currencyName);

    String slot_machine_money_pot_clear(String name);

    String slot_machine_item_pot_not_enabled();

    String slot_machine_item_pot_empty();

    String slot_machine_item_pot_invalid_item_list(String cause);

    String slot_machine_item_pot_deposit(String item, String name);

    String slot_machine_item_pot_deposit_multiple(String items, String name);

    String slot_machine_item_pot_set(String name, String items);

    String slot_machine_item_pot_reset(String name, String items);

    String slot_machine_item_pot_clear(String name);

    String slot_machine_reload_failure(String cause);

    String slot_machine_reload(String name);

    String statistic_show_slot_machine(String name, String statistic);

    String statistic_player_not_existent();

    String statistic_show_player(String name, String statistic);

    String statistic_top_category_not_existent();

    String statistic_top_slot_machine_invalid_category();

    String statistic_top_slot_machine_not_existent();

    String statistic_top_slot_machine(String category, String top);

    String statistic_top_player_not_existent();

    String statistic_top_player(String category, String top);

    String statistic_reset_slot_machine_failure(String name, String cause);
    
    String statistic_reset_slot_machine_success(String name);

    String statistic_reset_player_failure(String name, String cause);
    
    String statistic_reset_player_success(String name);

    String sign_coin_shop_header();

    String sign_coin_shop_coins(int coins);

    String sign_coin_shop_price(double price);

    String sign_coin_shop_spacer();

    String sign_pot_money(double money);

    String sign_pot_items(int items);

    String sign_pot_spacer(String colorCode);

    String input_not_numeric(String input);

    String lower_than_number(int number);

    String equals_number(int number);

    String higher_than_number(double number);

    String invalid_amount(String cause);

    String and();

    String total_spins();

    String won_spins();

    String lost_spins();

    String coins_spent();

    String won_money();

    String won_items();
}